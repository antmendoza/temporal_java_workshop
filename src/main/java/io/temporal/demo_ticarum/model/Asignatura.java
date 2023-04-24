package io.temporal.demo_ticarum.model;

import java.util.Objects;

public class Asignatura {

  private String id;
  private String nombre;

  public Asignatura() {}

  public Asignatura(String id, String nombre) {
    this.id = id;
    this.nombre = nombre;
  }

  public String getNombre() {
    return nombre;
  }

  public String getId() {

    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Asignatura that = (Asignatura) o;
    return Objects.equals(id, that.id) && Objects.equals(nombre, that.nombre);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, nombre);
  }

  @Override
  public String toString() {
    return "Asignatura{" + "id='" + id + '\'' + ", nombre='" + nombre + '\'' + '}';
  }
}
