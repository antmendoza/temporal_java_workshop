/*
 *  Copyright (c) 2020 Temporal Technologies, Inc. All Rights Reserved
 *
 *  Copyright 2012-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 *  Modifications copyright (C) 2017 Uber Technologies, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"). You may not
 *  use this file except in compliance with the License. A copy of the License is
 *  located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 *  or in the "license" file accompanying this file. This file is distributed on
 *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *  express or implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 */

package io.temporal.service;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import java.util.Objects;

@ActivityInterface
public interface AccountService {

  @ActivityMethod
  void deposit(DepositRequest depositRequest);

  @ActivityMethod
  void withdraw(WithdrawRequest withdrawRequest);

  final class DepositRequest {
    private String accountId;
    private String referenceId;
    private double amount;

    public DepositRequest() {}

    public DepositRequest(String accountId, String referenceId, double amount) {
      this.accountId = accountId;
      this.referenceId = referenceId;
      this.amount = amount;
    }

    public String accountId() {
      return accountId;
    }

    public String referenceId() {
      return referenceId;
    }

    public double amount() {
      return amount;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == this) return true;
      if (obj == null || obj.getClass() != this.getClass()) return false;
      DepositRequest that = (DepositRequest) obj;
      return Objects.equals(this.accountId, that.accountId)
          && Objects.equals(this.referenceId, that.referenceId)
          && Double.doubleToLongBits(this.amount) == Double.doubleToLongBits(that.amount);
    }

    @Override
    public int hashCode() {
      return Objects.hash(accountId, referenceId, amount);
    }

    @Override
    public String toString() {
      return "DepositRequest["
          + "accountId="
          + accountId
          + ", "
          + "referenceId="
          + referenceId
          + ", "
          + "amount="
          + amount
          + ']';
    }
  }

  final class WithdrawRequest {
    private String accountId;
    private String referenceId;
    private double amount;

    public WithdrawRequest() {}

    public WithdrawRequest(String accountId, String referenceId, double amount) {
      this.accountId = accountId;
      this.referenceId = referenceId;
      this.amount = amount;
    }

    public String accountId() {
      return accountId;
    }

    public String referenceId() {
      return referenceId;
    }

    public double amount() {
      return amount;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == this) return true;
      if (obj == null || obj.getClass() != this.getClass()) return false;
      WithdrawRequest that = (WithdrawRequest) obj;
      return Objects.equals(this.accountId, that.accountId)
          && Objects.equals(this.referenceId, that.referenceId)
          && Double.doubleToLongBits(this.amount) == Double.doubleToLongBits(that.amount);
    }

    @Override
    public int hashCode() {
      return Objects.hash(accountId, referenceId, amount);
    }

    @Override
    public String toString() {
      return "WithdrawRequest["
          + "accountId="
          + accountId
          + ", "
          + "referenceId="
          + referenceId
          + ", "
          + "amount="
          + amount
          + ']';
    }
  }
}
