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

import com.google.common.base.Strings;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlUtil {

  // parses the URL from the href attribute
  private static final Pattern hrefUrlPattern = Pattern.compile(".*<a href=\"([^\"]+)\">.*");
  // parses the text from the element's text, i.e. between <a> and </a> tags
  private static final Pattern linkTextPattern = Pattern.compile(".*<a[^\\]]*>([^<]+)</a>.*");

  private UrlUtil(){
  }

  /**
   * Parses a string that is assumed to contain an {@code <a>} HTML element. It extracts the
   * contents of the {@code href}. If it fails, then it extracts the text between the opening and
   * closing tags.
   * <p>
   * The string:
   * <ul>
   *  <li>may contain characters before and after the {@code <a>} element
   *  <li>must be one line
   *  <li>the {@code <a>} and the {@code href} string literals must be lower case
   * </ul>
   * @return either the value of the {@code href} attribute, or if it was not found then the text
   * content of the {@code <a>} element, or null if neither was found
   */
  public static String parseUrlFromHtmlLink(String text) {
    if (Strings.isNullOrEmpty(text)) {
      return null;
    }
    Matcher hrefMatcher = hrefUrlPattern.matcher(text);
    if (hrefMatcher.matches() && hrefMatcher.groupCount() == 1) {
      return hrefMatcher.group(1);
    }

    Matcher linkTextMatcher = linkTextPattern.matcher(text);
    if (linkTextMatcher.matches() && linkTextMatcher.groupCount() == 1) {
      return linkTextMatcher.group(1);
    }

    return null;
  }
}
