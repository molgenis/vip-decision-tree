package org.molgenis.vcf.decisiontree.filter.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MissingFieldTest {

  private MissingField field;

  @BeforeEach
  void setUp() {
    field = new MissingField("id");
  }

  @Test
  void getFieldType() {
    assertThrows(UnsupportedOperationException.class, () -> field.getFieldType());
  }

  @Test
  void getValueType() {
    assertThrows(UnsupportedOperationException.class, () -> field.getValueType());
  }

  @Test
  void getValueCount() {
    assertThrows(UnsupportedOperationException.class, () -> field.getValueCount());
  }

  @Test
  void getCount() {
    assertThrows(UnsupportedOperationException.class, () -> field.getCount());
  }

  @Test
  void getSeparator() {
    assertThrows(UnsupportedOperationException.class, () -> field.getSeparator());
  }

  @Test
  void getId() {
    assertEquals("id", field.getId());
  }
}