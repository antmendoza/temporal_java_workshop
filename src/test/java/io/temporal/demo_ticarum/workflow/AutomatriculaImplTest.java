package io.temporal.demo_ticarum.workflow;

import static org.mockito.Mockito.*;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.demo_ticarum.workflow.activity.AutomatriculaServiceImpl;
import io.temporal.demo_ticarum.workflow.activity.EmailService;
import io.temporal.testing.TestWorkflowRule;
import io.temporal.worker.Worker;
import java.time.Duration;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

public class AutomatriculaImplTest {

  private final String myWorkflowId = "myWorkflow";

  @Rule
  public TestWorkflowRule testWorkflowRule =
      TestWorkflowRule.newBuilder().setDoNotStart(true).build();

  @Test
  public void testStart() {

    EmailService emailService = mock(EmailService.class);
    Worker worker = testWorkflowRule.getWorker();

    worker.registerWorkflowImplementationTypes(AutomatriculaImpl.class);
    worker.registerActivitiesImplementations(emailService);
    worker.registerActivitiesImplementations(new AutomatriculaServiceImpl());

    // Start server
    testWorkflowRule.getTestEnvironment().start();

    final WorkflowOptions options =
        WorkflowOptions.newBuilder()
            .setWorkflowId(myWorkflowId)
            .setTaskQueue(testWorkflowRule.getTaskQueue())
            .build();

    final WorkflowClient workflowClient = testWorkflowRule.getWorkflowClient();

    ///////////

    final Automatricula workflow = workflowClient.newWorkflowStub(Automatricula.class, options);

    String alumno_id_57 = "alumno_ID_57";
    final AutomatriculaRequest automatriculaRequest =
        new AutomatriculaRequest(alumno_id_57, "2_TELECO");

    // Start workflow
    WorkflowClient.start(workflow::start, automatriculaRequest);

    Assert.assertEquals(workflow.getAsignaturasElegidas().size(), 0);
    workflow.seleccionarAsignatura("asignatura-2");

    Assert.assertEquals(workflow.getAsignaturasElegidas().size(), 1);
    workflow.seleccionarAsignatura("asignatura-3");

    Assert.assertEquals(workflow.getAsignaturasElegidas().size(), 2);
    workflow.borrarAsignatura("asignatura-3");

    Assert.assertEquals(workflow.getAsignaturasElegidas().size(), 1);

    workflow.finalizar();

    verify(emailService, times(1))
        .enviarNotificacionPago(workflow.getAsignaturasElegidas(), automatriculaRequest);
  }

  @Test
  public void testEnviarRecordatorio() {

    EmailService emailService = mock(EmailService.class);
    Worker worker = testWorkflowRule.getWorker();

    worker.registerWorkflowImplementationTypes(AutomatriculaImpl.class);
    worker.registerActivitiesImplementations(emailService);
    worker.registerActivitiesImplementations(new AutomatriculaServiceImpl());

    // Start server
    testWorkflowRule.getTestEnvironment().start();

    final WorkflowOptions options =
        WorkflowOptions.newBuilder()
            .setWorkflowId(myWorkflowId)
            .setTaskQueue(testWorkflowRule.getTaskQueue())
            .build();

    final WorkflowClient workflowClient = testWorkflowRule.getWorkflowClient();

    ///////////

    final Automatricula workflow = workflowClient.newWorkflowStub(Automatricula.class, options);

    String alumno_id_57 = "alumno_ID_57";
    final AutomatriculaRequest automatriculaRequest =
        new AutomatriculaRequest(alumno_id_57, "2_TELECO");

    // Start workflow
    WorkflowClient.start(workflow::start, automatriculaRequest);

    testWorkflowRule.getTestEnvironment().sleep(Duration.ofDays(3));

    verify(emailService, times(1)).enviarEmailRecordatorio(alumno_id_57);
  }
}
