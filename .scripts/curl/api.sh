#!/usr/bin/env bash

# Invoke update process
curl -XPOST 'localhost:8080/v1/orders/update-executions/orders.csv'

# Get order by id
curl -XGET 'localhost:8080/v1/orders/1'

# Get orders by business id
curl -XGET 'localhost:8080/v1/orders?businessId=10000000-0000-0000-0000-000000000000'