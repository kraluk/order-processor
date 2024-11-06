package io.kraluk.orderprocessor.test;

import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

public final class NoOpTransactionManager extends AbstractPlatformTransactionManager {

  @Override
  protected Object doGetTransaction() throws TransactionException {
    return new Object();
  }

  @Override
  protected void doBegin(final Object transaction, final TransactionDefinition definition) throws TransactionException {
    // noop
  }

  @Override
  protected void doCommit(final DefaultTransactionStatus status) throws TransactionException {
    // noop
  }

  @Override
  protected void doRollback(final DefaultTransactionStatus status) throws TransactionException {
    // noop
  }
}