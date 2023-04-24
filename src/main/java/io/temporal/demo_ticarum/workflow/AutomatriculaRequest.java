package io.temporal.demo_ticarum.workflow;

import java.util.Objects;

public class AutomatriculaRequest {

  private String alumnoId;
  private String curso;

  public AutomatriculaRequest() {}

  public AutomatriculaRequest(String alumnoId, String curso) {
    this.alumnoId = alumnoId;
    this.curso = curso;
  }

  public String getAlumnoId() {
    return alumnoId;
  }

  public String getCurso() {
    return curso;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AutomatriculaRequest that = (AutomatriculaRequest) o;
    return Objects.equals(alumnoId, that.alumnoId) && Objects.equals(curso, that.curso);
  }

  @Override
  public int hashCode() {
    return Objects.hash(alumnoId, curso);
  }

  @Override
  public String toString() {
    return "AutomatriculaRequest{"
        + "alumnoId='"
        + alumnoId
        + '\''
        + ", curso='"
        + curso
        + '\''
        + '}';
  }
}
