/*
 * Copyright © 2020 camunda services GmbH (info@camunda.com)
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
package io.atomix.raft.storage.log.entry;

import static com.google.common.base.MoreObjects.toStringHelper;

import io.atomix.storage.protocol.EntryType;
import java.util.Objects;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;

/**
 * Stores an entry that contains serialized records, ordered by their position; the lowestPosition
 * and highestPosition metadata allow for fast binary search over a collection of entries to quickly
 * find a particular record.
 *
 * <p>Each entry is written with the leader's {@link #timestamp() timestamp} at the time the entry
 * was logged This gives state machines an approximation of time with which to react to the
 * application of entries to the state machine.
 */
public class ZeebeEntry implements EntryValue {

  private final long lowestPosition;
  private final long highestPosition;
  private final DirectBuffer data;
  private final long term;
  private final long timestamp;

  public ZeebeEntry(
      final long term,
      final long timestamp,
      final long lowestPosition,
      final long highestPosition,
      final DirectBuffer data) {
    this.term = term;
    this.timestamp = timestamp;
    this.lowestPosition = lowestPosition;
    this.highestPosition = highestPosition;
    this.data = data;
  }

  public long lowestPosition() {
    return lowestPosition;
  }

  public long highestPosition() {
    return highestPosition;
  }

  public DirectBuffer data() {
    return data;
  }

  @Override
  public long term() {
    return term;
  }

  @Override
  public long timestamp() {
    return timestamp;
  }

  @Override
  public EntryType type() {
    return EntryType.ZEEBE;
  }

  @Override
  public int serialize(
      final EntrySerializer serializer, final MutableDirectBuffer dest, final int offset) {
    return serializer.serializeZeebeEntry(dest, offset, this);
  }

  @Override
  public int hashCode() {
    return Objects.hash(lowestPosition, highestPosition, data, term, timestamp);
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final ZeebeEntry that = (ZeebeEntry) o;
    return lowestPosition == that.lowestPosition
        && highestPosition == that.highestPosition
        && term == that.term
        && timestamp == that.timestamp
        && data.equals(that.data);
  }

  @Override
  public String toString() {
    return toStringHelper(this)
        .add("term", term())
        .add("timestamp", timestamp())
        .add("lowestPosition", lowestPosition())
        .add("highestPosition", highestPosition())
        .add("term", term())
        .add("timestamp", timestamp())
        .toString();
  }
}