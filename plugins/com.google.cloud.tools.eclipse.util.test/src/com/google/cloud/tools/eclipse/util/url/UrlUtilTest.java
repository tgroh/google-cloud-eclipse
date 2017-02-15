/*
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.tools.eclipse.util.url;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class UrlUtilTest {

  @Test
  public void testParseUrlFromHtmlLink_nullText() {
    assertNull(UrlUtil.parseUrlFromHtmlLink(null));
  }

  @Test
  public void testParseUrlFromHtmlLink_emptyText() {
    assertNull(UrlUtil.parseUrlFromHtmlLink(""));
  }

  @Test
  public void testParseUrlFromHtmlLink_noAElement() {
    assertNull(UrlUtil.parseUrlFromHtmlLink("no 'a' element here"));
  }

  @Test
  public void testParseUrlFromHtmlLink_noHref() {
    assertThat(UrlUtil.parseUrlFromHtmlLink("prefix<a>http://example.com</a>postfix"),
               is("http://example.com"));
  }

  @Test
  public void testParseUrlFromHtmlLink_noHrefMissingClosingTag() {
    assertNull(UrlUtil.parseUrlFromHtmlLink("prefix<a>http://example.compostfix"));
  }

  @Test
  public void testParseUrlFromHtmlLink_hrefHasValue() {
    assertThat(UrlUtil.parseUrlFromHtmlLink("prefix<a href=\"http://example.com\">postfix"),
               is("http://example.com"));
  }

  @Test
  public void testParseUrlFromHtmlLink_hrefEmpty() {
    assertThat(UrlUtil.parseUrlFromHtmlLink("prefix<a href=\"\">example.com</a>"),
               is("example.com"));
  }
}
