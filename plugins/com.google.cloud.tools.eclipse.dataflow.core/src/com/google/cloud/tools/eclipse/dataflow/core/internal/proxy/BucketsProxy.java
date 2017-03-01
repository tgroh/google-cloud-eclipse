/*
 * Copyright (C) 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.cloud.tools.eclipse.dataflow.core.internal.proxy;

import com.google.api.services.storage.model.Bucket;
import com.google.api.services.storage.model.Buckets;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * A proxy to wrap {@code Buckets}.
 *
 * <p>Used to hide the fact that the underlying {@code Buckets} implementation can be repackaged so
 * unit tests in another module can mock out the service calls appropriately.
 */
public class BucketsProxy {
  private static final Function<Bucket, BucketProxy> BUCKET_TO_PROXY_FN =
      new Function<Bucket, BucketProxy>() {
        @Override
    public BucketProxy apply(Bucket bucket) {
      return BucketProxy.of(bucket);
    }
  };

  private Buckets buckets;

  BucketsProxy(Buckets buckets) {
    this.buckets = buckets;
  }

  public List<BucketProxy> getItems() {
    return Lists.transform(buckets.getItems(), BUCKET_TO_PROXY_FN);
  }
}
