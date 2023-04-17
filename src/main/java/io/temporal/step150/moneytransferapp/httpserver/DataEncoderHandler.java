/*
 * Copyright (C) 2022 Temporal Technologies, Inc. All Rights Reserved.
 *
 * Copyright (C) 2012-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Modifications copyright (C) 2017 Uber Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this material except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.temporal.step150.moneytransferapp.httpserver;

import com.google.common.net.HttpHeaders;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.temporal.api.common.v1.Payload;
import io.temporal.api.common.v1.Payloads;
import io.temporal.payload.codec.ChainCodec;
import io.temporal.payload.codec.PayloadCodec;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

class DataEncoderHandler implements HttpHandler {
    private final PayloadCodec codec;

    public DataEncoderHandler(List<PayloadCodec> codecs) {
        this.codec = new ChainCodec(codecs);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        exchange.getRequestHeaders().get("Origin");
        List<String> namespace = exchange.getRequestHeaders().get("X-Namespace");

        Headers headers = exchange.getRequestHeaders();


        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }


        boolean isMethodPOST = exchange.getRequestMethod().equals("POST");
        boolean isMethodOPTIONS = exchange.getRequestMethod().equalsIgnoreCase("OPTIONS");
        if (!isMethodPOST && !isMethodOPTIONS) {
            exchange.sendResponseHeaders(HttpServletResponse.SC_METHOD_NOT_ALLOWED, -1);
            return;
        }


        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Credentials", "true");

        if (isMethodOPTIONS) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }


        String contentType = exchange.getRequestHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
        if (contentType == null
                || !contentType.startsWith(Constants.CONTENT_TYPE_APPLICATION_JSON)) {
            exchange.sendResponseHeaders(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, -1);
            return;
        }

        String path = exchange.getRequestURI().getPath();
        boolean encode;
        if (path.endsWith(Constants.ENCODE_PATH_POSTFIX)) {
            encode = true;
        } else if (path.endsWith(Constants.DECODE_PATH_POSTFIX)) {
            encode = false;
        } else {
            exchange.sendResponseHeaders(HttpServletResponse.SC_NOT_FOUND, -1);
            return;
        }

        Payloads.Builder incomingPayloads = Payloads.newBuilder();
        try (InputStreamReader ioReader = new InputStreamReader(exchange.getRequestBody())) {
            Constants.JSON_FORMAT.merge(ioReader, incomingPayloads);
        } catch (IOException e) {
            exchange.sendResponseHeaders(HttpServletResponse.SC_BAD_REQUEST, -1);
            return;
        }

        List<Payload> incomingPayloadsList = incomingPayloads.build().getPayloadsList();
        List<Payload> outgoingPayloadsList =
                encode ? codec.encode(incomingPayloadsList) : codec.decode(incomingPayloadsList);

        exchange
                .getResponseHeaders()
                .add(
                        HttpHeaders.CONTENT_TYPE, Constants.CONTENT_TYPE_APPLICATION_JSON);
        exchange.sendResponseHeaders(HttpServletResponse.SC_OK, 0);
        try (OutputStreamWriter out = new OutputStreamWriter(exchange.getResponseBody())) {
            Constants.JSON_PRINTER.appendTo(
                    Payloads.newBuilder().addAllPayloads(outgoingPayloadsList).build(), out);
        }
        exchange.close();
    }
}
