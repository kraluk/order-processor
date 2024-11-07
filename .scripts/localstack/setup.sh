#!/usr/bin/env bash

awslocal s3 mb s3://order-updates-local
awslocal sqs create-queue --queue-name order-updates-local

awslocal s3 cp '/etc/localstack/init/ready.d/orders.csv' 's3://order-updates-local/orders.csv'
