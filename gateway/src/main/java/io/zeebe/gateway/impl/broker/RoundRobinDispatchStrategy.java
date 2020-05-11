/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH under
 * one or more contributor license agreements. See the NOTICE file distributed
 * with this work for additional information regarding copyright ownership.
 * Licensed under the Zeebe Community License 1.0. You may not use this file
 * except in compliance with the Zeebe Community License 1.0.
 */
package io.zeebe.gateway.impl.broker;

import io.zeebe.gateway.impl.broker.cluster.BrokerClusterState;
import io.zeebe.gateway.impl.broker.cluster.BrokerTopologyManager;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinDispatchStrategy implements RequestDispatchStrategy {

  protected final BrokerTopologyManager topologyManager;
  protected final AtomicInteger partitions = new AtomicInteger(0);

  public RoundRobinDispatchStrategy(final BrokerTopologyManager topologyManager) {
    this.topologyManager = topologyManager;
  }

  @Override
  public int determinePartition() {
    final BrokerClusterState topology = topologyManager.getTopology();

    if (topology != null) {
      // go over all partitions once
      for (int i = 0; i < topology.getPartitionsCount(); i++) {
        final int offset = partitions.getAndIncrement();
        final int partition = topology.getPartition(offset);
        if (topology.getLeaderForPartition(partition) != BrokerClusterState.NODE_ID_NULL) {
          return partition;
        }
      }
    }

    return BrokerClusterState.PARTITION_ID_NULL;
  }
}
