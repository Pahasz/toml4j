package com.moandjiezana.toml;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Calendar;
import java.util.TimeZone;

import org.junit.Test;

public class TomlTest {

  @Test
  public void should_get_string() throws Exception {
    Toml toml = new Toml().parse("a = \"a\"");

    assertEquals("a", toml.getString("a"));
  }

  @Test
  public void should_get_number() throws Exception {
    Toml toml = new Toml().parse("b = 1001");

    assertEquals(1001, toml.getLong("b").intValue());
  }

  @Test
  public void should_get_list() throws Exception {
    Toml toml = new Toml().parse("list = [\"a\", \"b\", \"c\"]");

    assertEquals(asList("a", "b", "c"), toml.getList("list", String.class));
  }

  @Test
  public void should_get_boolean() throws Exception {
    Toml toml = new Toml().parse("bool_false = false\nbool_true = true");

    assertFalse(toml.getBoolean("bool_false"));
    assertTrue(toml.getBoolean("bool_true"));
  }

  @Test
  public void should_get_date() throws Exception {
    Toml toml = new Toml().parse("a_date = 2011-11-10T13:12:00Z");

    Calendar calendar = Calendar.getInstance();
    calendar.set(2011, Calendar.NOVEMBER, 10, 13, 12, 00);
    calendar.set(Calendar.MILLISECOND, 0);
    calendar.setTimeZone(TimeZone.getTimeZone("UTC"));

    assertEquals(calendar.getTime(), toml.getDate("a_date"));
  }

  @Test
  public void should_get_double() throws Exception {
    Toml toml = new Toml().parse("double = 5.25");

    assertEquals(5.25D, toml.getDouble("double").doubleValue(), 0.0);
  }

  @Test
  public void should_get_key_group() throws Exception {
    Toml toml = new Toml().parse("[group]\nkey = \"value\"");

    Toml group = toml.getKeyGroup("group");

    assertEquals("value", group.getString("key"));
  }

  @Test
  public void should_get_value_for_multi_key() throws Exception {
    Toml toml = new Toml().parse("[group]\nkey = \"value\"");

    assertEquals("value", toml.getString("group.key"));
  }

  @Test
  public void should_get_value_for_multi_key_with_no_parent_key_group() throws Exception {
    Toml toml = new Toml().parse("[group.sub]\nkey = \"value\"");

    assertEquals("value", toml.getString("group.sub.key"));
  }

  @Test
  public void should_get_key_group_for_multi_key() throws Exception {
    Toml toml = new Toml().parse("[group]\nother=1\n[group.sub]\nkey = \"value\"");

    assertEquals("value", toml.getKeyGroup("group.sub").getString("key"));
  }

  @Test
  public void should_get_key_group_for_multi_key_with_no_parent_key_group() throws Exception {
    Toml toml = new Toml().parse("[group.sub]\nkey = \"value\"");

    assertEquals("value", toml.getKeyGroup("group.sub").getString("key"));
  }

  @Test
  public void should_return_null_if_no_value_for_key() throws Exception {
    Toml toml = new Toml().parse("");

    assertNull(toml.getString("a"));
  }

  @Test
  public void should_return_null_when_no_value_for_multi_key() throws Exception {
    Toml toml = new Toml().parse("");

    assertNull(toml.getString("group.key"));
  }

  @Test
  public void should_load_from_file() throws Exception {
    Toml toml = new Toml().parse(new File(getClass().getResource("should_load_from_file.toml").getFile()));

    assertEquals("value", toml.getString("key"));
  }

  @Test
  public void should_support_numbers_in_key_names() throws Exception {
    Toml toml = new Toml().parse("a1 = 1");

    assertEquals(1, toml.getLong("a1").intValue());
  }

  @Test
  public void should_support_numbers_in_key_group_names() throws Exception {
    Toml toml = new Toml().parse("[group1]\na = 1");

    assertEquals(1, toml.getLong("group1.a").intValue());
  }

  @Test
  public void should_support_underscores_in_key_names() throws Exception {
    Toml toml = new Toml().parse("a_a = 1");

    assertEquals(1, toml.getLong("a_a").intValue());
  }

  @Test
  public void should_support_underscores_in_key_group_names() throws Exception {
    Toml toml = new Toml().parse("[group_a]\na = 1");

    assertEquals(1, toml.getLong("group_a.a").intValue());
  }

  @Test(expected = IllegalStateException.class)
  public void should_fail_when_dot_in_key_name() throws Exception {
    new Toml().parse("a.a = 1");
  }

  @Test(expected = IllegalStateException.class)
  public void should_fail_on_invalid_date() throws Exception {
    new Toml().parse("d = 2012-13-01T15:00:00Z");
  }

  @Test(expected = IllegalStateException.class)
  public void should_fail_when_key_is_overwritten_by_key_group() {
    new Toml().parse("[fruit]\ntype=\"apple\"\n[fruit.type]\napple=\"yes\"");
  }

  @Test(expected = IllegalStateException.class)
  public void should_fail_when_key_is_overwritten_by_another_key() {
    new Toml().parse("[fruit]\ntype=\"apple\"\ntype=\"orange\"");
  }
}
