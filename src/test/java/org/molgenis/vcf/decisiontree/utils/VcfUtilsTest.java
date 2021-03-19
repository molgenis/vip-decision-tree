package org.molgenis.vcf.decisiontree.utils;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import htsjdk.variant.variantcontext.VariantContext;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.molgenis.vcf.decisiontree.filter.model.FieldImpl;

class VcfUtilsTest {

  @Test
  void getInfoAsInteger() {
    String key = "my_key";
    FieldImpl field = when(mock(FieldImpl.class).getId()).thenReturn(key).getMock();
    Integer value = 1;
    VariantContext variantContext =
        when(mock(VariantContext.class).getAttribute(key)).thenReturn(value).getMock();

    assertEquals(1, VcfUtils.getInfoAsInteger(variantContext, field));
  }

  @Test
  void getInfoAsIntegerFromString() {
    String key = "my_key";
    FieldImpl field = when(mock(FieldImpl.class).getId()).thenReturn(key).getMock();
    String value = "1";
    VariantContext variantContext =
        when(mock(VariantContext.class).getAttribute(key)).thenReturn(value).getMock();

    assertEquals(1, VcfUtils.getInfoAsInteger(variantContext, field));
  }

  @Test
  void getInfoAsIntegerFromStringMissing() {
    String key = "my_key";
    FieldImpl field = when(mock(FieldImpl.class).getId()).thenReturn(key).getMock();
    String value = ".";
    VariantContext variantContext =
        when(mock(VariantContext.class).getAttribute(key)).thenReturn(value).getMock();

    assertNull(VcfUtils.getInfoAsInteger(variantContext, field));
  }

  @Test
  void getInfoAsIntegerFromNull() {
    String key = "my_key";
    FieldImpl field = when(mock(FieldImpl.class).getId()).thenReturn(key).getMock();
    Integer value = null;
    VariantContext variantContext =
        when(mock(VariantContext.class).getAttribute(key)).thenReturn(value).getMock();

    assertNull(VcfUtils.getInfoAsInteger(variantContext, field));
  }

  @Test
  void getInfoAsIntegerInvalidType() {
    String key = "my_key";
    FieldImpl field = when(mock(FieldImpl.class).getId()).thenReturn(key).getMock();
    Double value = 1.23;
    VariantContext variantContext =
        when(mock(VariantContext.class).getAttribute(key)).thenReturn(value).getMock();

    assertThrows(
        TypeConversionException.class, () -> VcfUtils.getInfoAsInteger(variantContext, field));
  }

  @Test
  void getInfoAsIntegerList() {
    String key = "my_key";
    FieldImpl field = when(mock(FieldImpl.class).getId()).thenReturn(key).getMock();
    List<Integer> values = asList(1, 2);
    VariantContext variantContext =
        when(mock(VariantContext.class).getAttribute(key)).thenReturn(values).getMock();

    assertEquals(values, VcfUtils.getInfoAsIntegerList(variantContext, field));
  }

  @Test
  void getInfoAsIntegerListSingleton() {
    String key = "my_key";
    FieldImpl field = when(mock(FieldImpl.class).getId()).thenReturn(key).getMock();
    List<Integer> values = singletonList(1);
    VariantContext variantContext =
        when(mock(VariantContext.class).getAttribute(key)).thenReturn(values).getMock();

    assertEquals(values, VcfUtils.getInfoAsIntegerList(variantContext, field));
  }

  @Test
  void getInfoAsIntegerListSingletonString() {
    String key = "my_key";
    FieldImpl field = when(mock(FieldImpl.class).getId()).thenReturn(key).getMock();
    List<String> values = singletonList("1");
    VariantContext variantContext =
        when(mock(VariantContext.class).getAttribute(key)).thenReturn(values).getMock();

    assertEquals(singletonList(1), VcfUtils.getInfoAsIntegerList(variantContext, field));
  }

  @Test
  void getInfoAsIntegerListSingletonStringMissing() {
    String key = "my_key";
    FieldImpl field = when(mock(FieldImpl.class).getId()).thenReturn(key).getMock();
    List<String> values = singletonList(".");
    VariantContext variantContext =
        when(mock(VariantContext.class).getAttribute(key)).thenReturn(values).getMock();

    assertEquals(singletonList(null), VcfUtils.getInfoAsIntegerList(variantContext, field));
  }

  @Test
  void getInfoAsIntegerListNull() {
    String key = "my_key";
    FieldImpl field = when(mock(FieldImpl.class).getId()).thenReturn(key).getMock();
    List<Integer> values = null;
    VariantContext variantContext =
        when(mock(VariantContext.class).getAttribute(key)).thenReturn(values).getMock();

    assertEquals(emptyList(), VcfUtils.getInfoAsIntegerList(variantContext, field));
  }

  @Test
  void getInfoAsIntegerListEmptyList() {
    String key = "my_key";
    FieldImpl field = when(mock(FieldImpl.class).getId()).thenReturn(key).getMock();
    List<Integer> values = emptyList();
    VariantContext variantContext =
        when(mock(VariantContext.class).getAttribute(key)).thenReturn(values).getMock();

    assertEquals(emptyList(), VcfUtils.getInfoAsIntegerList(variantContext, field));
  }

  @Test
  void getInfoAsIntegerListFromStringList() {
    String key = "my_key";
    FieldImpl field = when(mock(FieldImpl.class).getId()).thenReturn(key).getMock();
    List<String> values = asList("1", "2");
    VariantContext variantContext =
        when(mock(VariantContext.class).getAttribute(key)).thenReturn(values).getMock();

    assertEquals(asList(1, 2), VcfUtils.getInfoAsIntegerList(variantContext, field));
  }

  @Test
  void getInfoAsIntegerListFromStringListMissing() {
    String key = "my_key";
    FieldImpl field = when(mock(FieldImpl.class).getId()).thenReturn(key).getMock();
    List<String> values = asList(".", ".");
    VariantContext variantContext =
        when(mock(VariantContext.class).getAttribute(key)).thenReturn(values).getMock();

    assertEquals(asList(null, null), VcfUtils.getInfoAsIntegerList(variantContext, field));
  }

  @Test
  void getInfoAsIntegerListFromString() {
    String key = "my_key";
    FieldImpl field = when(mock(FieldImpl.class).getId()).thenReturn(key).getMock();
    String value = "1";
    VariantContext variantContext =
        when(mock(VariantContext.class).getAttribute(key)).thenReturn(value).getMock();

    assertEquals(singletonList(1), VcfUtils.getInfoAsIntegerList(variantContext, field));
  }

  @Test
  void getInfoAsIntegerListFromStringMissing() {
    String key = "my_key";
    FieldImpl field = when(mock(FieldImpl.class).getId()).thenReturn(key).getMock();
    String value = ".";
    VariantContext variantContext =
        when(mock(VariantContext.class).getAttribute(key)).thenReturn(value).getMock();

    assertEquals(singletonList(null), VcfUtils.getInfoAsIntegerList(variantContext, field));
  }

  @Test
  void getInfoAsIntegerListInvalidType() {
    String key = "my_key";
    FieldImpl field = when(mock(FieldImpl.class).getId()).thenReturn(key).getMock();
    Double value = 1.;
    VariantContext variantContext =
        when(mock(VariantContext.class).getAttribute(key)).thenReturn(value).getMock();

    assertThrows(
        TypeConversionException.class, () -> VcfUtils.getInfoAsIntegerList(variantContext, field));
  }

  @Test
  void getInfoAsDouble() {
    String key = "my_key";
    FieldImpl field = when(mock(FieldImpl.class).getId()).thenReturn(key).getMock();
    Double value = 1.;
    VariantContext variantContext =
        when(mock(VariantContext.class).getAttribute(key)).thenReturn(value).getMock();

    assertEquals(1., VcfUtils.getInfoAsDouble(variantContext, field), 1E-6);
  }

  @Test
  void getInfoAsDoubleFromString() {
    String key = "my_key";
    FieldImpl field = when(mock(FieldImpl.class).getId()).thenReturn(key).getMock();
    String value = "1";
    VariantContext variantContext =
        when(mock(VariantContext.class).getAttribute(key)).thenReturn(value).getMock();

    assertEquals(1, VcfUtils.getInfoAsDouble(variantContext, field));
  }

  @Test
  void getInfoAsDoubleFromStringMissing() {
    String key = "my_key";
    FieldImpl field = when(mock(FieldImpl.class).getId()).thenReturn(key).getMock();
    String value = ".";
    VariantContext variantContext =
        when(mock(VariantContext.class).getAttribute(key)).thenReturn(value).getMock();

    assertNull(VcfUtils.getInfoAsDouble(variantContext, field));
  }

  @Test
  void getInfoAsDoubleFromNull() {
    String key = "my_key";
    FieldImpl field = when(mock(FieldImpl.class).getId()).thenReturn(key).getMock();
    Double value = null;
    VariantContext variantContext =
        when(mock(VariantContext.class).getAttribute(key)).thenReturn(value).getMock();

    assertNull(VcfUtils.getInfoAsDouble(variantContext, field));
  }

  @Test
  void getInfoAsDoubleInvalidType() {
    String key = "my_key";
    FieldImpl field = when(mock(FieldImpl.class).getId()).thenReturn(key).getMock();
    Integer value = 1;
    VariantContext variantContext =
        when(mock(VariantContext.class).getAttribute(key)).thenReturn(value).getMock();

    assertThrows(
        TypeConversionException.class, () -> VcfUtils.getInfoAsDouble(variantContext, field));
  }

  @Test
  void getInfoAsDoubleList() {
    String key = "my_key";
    FieldImpl field = when(mock(FieldImpl.class).getId()).thenReturn(key).getMock();
    List<Double> values = asList(1., 2.);
    VariantContext variantContext =
        when(mock(VariantContext.class).getAttribute(key)).thenReturn(values).getMock();

    List<Double> actualValues = VcfUtils.getInfoAsDoubleList(variantContext, field);
    assertAll(
        () -> assertEquals(2, actualValues.size()),
        () -> assertEquals(1., actualValues.get(0), 1E-6),
        () -> assertEquals(2., actualValues.get(1), 1E-6));
  }

  @Test
  void getInfoAsDoubleListSingleton() {
    String key = "my_key";
    FieldImpl field = when(mock(FieldImpl.class).getId()).thenReturn(key).getMock();
    List<Double> values = singletonList(1.);
    VariantContext variantContext =
        when(mock(VariantContext.class).getAttribute(key)).thenReturn(values).getMock();

    List<Double> actualValues = VcfUtils.getInfoAsDoubleList(variantContext, field);
    assertAll(
        () -> assertEquals(1, actualValues.size()),
        () -> assertEquals(1., actualValues.get(0), 1E-6));
  }

  @Test
  void getInfoAsDoubleListSingletonString() {
    String key = "my_key";
    FieldImpl field = when(mock(FieldImpl.class).getId()).thenReturn(key).getMock();
    List<String> values = singletonList("1.0");
    VariantContext variantContext =
        when(mock(VariantContext.class).getAttribute(key)).thenReturn(values).getMock();

    List<Double> actualValues = VcfUtils.getInfoAsDoubleList(variantContext, field);
    assertAll(
        () -> assertEquals(1, actualValues.size()),
        () -> assertEquals(1., actualValues.get(0), 1E-6));
  }

  @Test
  void getInfoAsDoubleListSingletonStringMissing() {
    String key = "my_key";
    FieldImpl field = when(mock(FieldImpl.class).getId()).thenReturn(key).getMock();
    List<String> values = singletonList(".");
    VariantContext variantContext =
        when(mock(VariantContext.class).getAttribute(key)).thenReturn(values).getMock();

    assertEquals(singletonList(null), VcfUtils.getInfoAsDoubleList(variantContext, field));
  }

  @Test
  void getInfoAsDoubleListNull() {
    String key = "my_key";
    FieldImpl field = when(mock(FieldImpl.class).getId()).thenReturn(key).getMock();
    List<Double> values = null;
    VariantContext variantContext =
        when(mock(VariantContext.class).getAttribute(key)).thenReturn(values).getMock();

    assertEquals(emptyList(), VcfUtils.getInfoAsDoubleList(variantContext, field));
  }

  @Test
  void getInfoAsDoubleListEmptyList() {
    String key = "my_key";
    FieldImpl field = when(mock(FieldImpl.class).getId()).thenReturn(key).getMock();
    List<Double> values = emptyList();
    VariantContext variantContext =
        when(mock(VariantContext.class).getAttribute(key)).thenReturn(values).getMock();

    assertEquals(emptyList(), VcfUtils.getInfoAsDoubleList(variantContext, field));
  }

  @Test
  void getInfoAsDoubleListFromStringList() {
    String key = "my_key";
    FieldImpl field = when(mock(FieldImpl.class).getId()).thenReturn(key).getMock();
    List<String> values = asList("1", "2");
    VariantContext variantContext =
        when(mock(VariantContext.class).getAttribute(key)).thenReturn(values).getMock();

    List<Double> actualValues = VcfUtils.getInfoAsDoubleList(variantContext, field);
    assertAll(
        () -> assertEquals(2, actualValues.size()),
        () -> assertEquals(1., actualValues.get(0), 1E-6),
        () -> assertEquals(2., actualValues.get(1), 1E-6));
  }

  @Test
  void getInfoAsDoubleListFromStringListMissing() {
    String key = "my_key";
    FieldImpl field = when(mock(FieldImpl.class).getId()).thenReturn(key).getMock();
    List<String> values = asList(".", ".");
    VariantContext variantContext =
        when(mock(VariantContext.class).getAttribute(key)).thenReturn(values).getMock();

    assertEquals(asList(null, null), VcfUtils.getInfoAsDoubleList(variantContext, field));
  }

  @Test
  void getInfoAsDoubleListFromString() {
    String key = "my_key";
    FieldImpl field = when(mock(FieldImpl.class).getId()).thenReturn(key).getMock();
    String value = "1";
    VariantContext variantContext =
        when(mock(VariantContext.class).getAttribute(key)).thenReturn(value).getMock();

    List<Double> actualValues = VcfUtils.getInfoAsDoubleList(variantContext, field);
    assertAll(
        () -> assertEquals(1, actualValues.size()),
        () -> assertEquals(1., actualValues.get(0), 1E-6));
  }

  @Test
  void getInfoAsDoubleListFromStringMissing() {
    String key = "my_key";
    FieldImpl field = when(mock(FieldImpl.class).getId()).thenReturn(key).getMock();
    String value = ".";
    VariantContext variantContext =
        when(mock(VariantContext.class).getAttribute(key)).thenReturn(value).getMock();

    assertEquals(singletonList(null), VcfUtils.getInfoAsDoubleList(variantContext, field));
  }

  @Test
  void getInfoAsDoubleListInvalidType() {
    String key = "my_key";
    FieldImpl field = when(mock(FieldImpl.class).getId()).thenReturn(key).getMock();
    Integer value = 1;
    VariantContext variantContext =
        when(mock(VariantContext.class).getAttribute(key)).thenReturn(value).getMock();

    assertThrows(
        TypeConversionException.class, () -> VcfUtils.getInfoAsDoubleList(variantContext, field));
  }

  @Test
  void getInfoAsString() {
    String key = "my_key";
    FieldImpl field = when(mock(FieldImpl.class).getId()).thenReturn(key).getMock();
    String value = "str1";
    VariantContext variantContext =
        when(mock(VariantContext.class).getAttribute(key)).thenReturn(value).getMock();

    assertEquals("str1", VcfUtils.getInfoAsString(variantContext, field));
  }

  @Test
  void getInfoAsStringFromMissing() {
    String key = "my_key";
    FieldImpl field = when(mock(FieldImpl.class).getId()).thenReturn(key).getMock();
    String value = ".";
    VariantContext variantContext =
        when(mock(VariantContext.class).getAttribute(key)).thenReturn(value).getMock();

    assertNull(VcfUtils.getInfoAsString(variantContext, field));
  }

  @Test
  void getInfoAsStringFromNull() {
    String key = "my_key";
    FieldImpl field = when(mock(FieldImpl.class).getId()).thenReturn(key).getMock();
    String value = null;
    VariantContext variantContext =
        when(mock(VariantContext.class).getAttribute(key)).thenReturn(value).getMock();

    assertNull(VcfUtils.getInfoAsString(variantContext, field));
  }

  @Test
  void getInfoAsStringInvalidType() {
    String key = "my_key";
    FieldImpl field = when(mock(FieldImpl.class).getId()).thenReturn(key).getMock();
    Double value = 1.23;
    VariantContext variantContext =
        when(mock(VariantContext.class).getAttribute(key)).thenReturn(value).getMock();

    assertThrows(
        TypeConversionException.class, () -> VcfUtils.getInfoAsString(variantContext, field));
  }

  @Test
  void getInfoAsStringList() {
    String key = "my_key";
    FieldImpl field = when(mock(FieldImpl.class).getId()).thenReturn(key).getMock();
    List<String> values = asList("1", "2");
    VariantContext variantContext =
        when(mock(VariantContext.class).getAttribute(key)).thenReturn(values).getMock();

    assertEquals(values, VcfUtils.getInfoAsStringList(variantContext, field));
  }

  @Test
  void getInfoAsStringListSingleton() {
    String key = "my_key";
    FieldImpl field = when(mock(FieldImpl.class).getId()).thenReturn(key).getMock();
    List<String> values = singletonList("1");
    VariantContext variantContext =
        when(mock(VariantContext.class).getAttribute(key)).thenReturn(values).getMock();

    assertEquals(values, VcfUtils.getInfoAsStringList(variantContext, field));
  }

  @Test
  void getInfoAsStringListSingletonMissing() {
    String key = "my_key";
    FieldImpl field = when(mock(FieldImpl.class).getId()).thenReturn(key).getMock();
    List<String> values = singletonList(".");
    VariantContext variantContext =
        when(mock(VariantContext.class).getAttribute(key)).thenReturn(values).getMock();

    assertEquals(singletonList(null), VcfUtils.getInfoAsStringList(variantContext, field));
  }

  @Test
  void getInfoAsStringListNull() {
    String key = "my_key";
    FieldImpl field = when(mock(FieldImpl.class).getId()).thenReturn(key).getMock();
    List<String> values = null;
    VariantContext variantContext =
        when(mock(VariantContext.class).getAttribute(key)).thenReturn(values).getMock();

    assertEquals(emptyList(), VcfUtils.getInfoAsStringList(variantContext, field));
  }

  @Test
  void getInfoAsStringListEmptyList() {
    String key = "my_key";
    FieldImpl field = when(mock(FieldImpl.class).getId()).thenReturn(key).getMock();
    List<String> values = emptyList();
    VariantContext variantContext =
        when(mock(VariantContext.class).getAttribute(key)).thenReturn(values).getMock();

    assertEquals(emptyList(), VcfUtils.getInfoAsStringList(variantContext, field));
  }

  @Test
  void getInfoAsStringListFromListMissing() {
    String key = "my_key";
    FieldImpl field = when(mock(FieldImpl.class).getId()).thenReturn(key).getMock();
    List<String> values = asList(".", ".");
    VariantContext variantContext =
        when(mock(VariantContext.class).getAttribute(key)).thenReturn(values).getMock();

    assertEquals(asList(null, null), VcfUtils.getInfoAsStringList(variantContext, field));
  }

  @Test
  void getInfoAsStringListFromString() {
    String key = "my_key";
    FieldImpl field = when(mock(FieldImpl.class).getId()).thenReturn(key).getMock();
    String value = "1";
    VariantContext variantContext =
        when(mock(VariantContext.class).getAttribute(key)).thenReturn(value).getMock();

    assertEquals(singletonList("1"), VcfUtils.getInfoAsStringList(variantContext, field));
  }

  @Test
  void getInfoAsStringListFromMissing() {
    String key = "my_key";
    FieldImpl field = when(mock(FieldImpl.class).getId()).thenReturn(key).getMock();
    String value = ".";
    VariantContext variantContext =
        when(mock(VariantContext.class).getAttribute(key)).thenReturn(value).getMock();

    assertEquals(singletonList(null), VcfUtils.getInfoAsStringList(variantContext, field));
  }

  @Test
  void getInfoAsStringListInvalidType() {
    String key = "my_key";
    FieldImpl field = when(mock(FieldImpl.class).getId()).thenReturn(key).getMock();
    Double value = 1.;
    VariantContext variantContext =
        when(mock(VariantContext.class).getAttribute(key)).thenReturn(value).getMock();

    assertThrows(
        TypeConversionException.class, () -> VcfUtils.getInfoAsStringList(variantContext, field));
  }

  @Test
  void getInfoAsBooleanTrue() {
    String key = "my_key";
    FieldImpl field = when(mock(FieldImpl.class).getId()).thenReturn(key).getMock();
    Boolean value = true;
    VariantContext variantContext =
        when(mock(VariantContext.class).getAttribute(key)).thenReturn(value).getMock();
    assertTrue(VcfUtils.getInfoAsBoolean(variantContext, field));
  }

  @Test
  void getInfoAsBooleanFalse() {
    String key = "my_key";
    FieldImpl field = when(mock(FieldImpl.class).getId()).thenReturn(key).getMock();
    Boolean value = false;
    VariantContext variantContext =
        when(mock(VariantContext.class).getAttribute(key)).thenReturn(value).getMock();
    assertFalse(VcfUtils.getInfoAsBoolean(variantContext, field));
  }

  @Test
  void getInfoAsBooleanNull() {
    String key = "my_key";
    FieldImpl field = when(mock(FieldImpl.class).getId()).thenReturn(key).getMock();
    Boolean value = null;
    VariantContext variantContext =
        when(mock(VariantContext.class).getAttribute(key)).thenReturn(value).getMock();
    assertFalse(VcfUtils.getInfoAsBoolean(variantContext, field));
  }

  @Test
  void getInfoAsBooleanInvalidType() {
    String key = "my_key";
    FieldImpl field = when(mock(FieldImpl.class).getId()).thenReturn(key).getMock();
    Double value = 1.;
    VariantContext variantContext =
        when(mock(VariantContext.class).getAttribute(key)).thenReturn(value).getMock();

    assertThrows(
        TypeConversionException.class, () -> VcfUtils.getInfoAsBoolean(variantContext, field));
  }
}
