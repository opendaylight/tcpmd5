/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.controller.config.yang.tcpmd5.jni.cfg;

import javax.management.InstanceAlreadyExistsException;
import org.junit.Before;
import org.junit.Test;
import org.opendaylight.controller.config.api.ConflictingVersionException;
import org.opendaylight.controller.config.api.ValidationException;
import org.opendaylight.controller.config.api.jmx.CommitStatus;
import org.opendaylight.controller.config.manager.impl.AbstractConfigTest;
import org.opendaylight.controller.config.manager.impl.factoriesresolver.HardcodedModuleFactoriesResolver;
import org.opendaylight.controller.config.util.ConfigTransactionJMXClient;
import org.opendaylight.tcpmd5.jni.NativeTestSupport;

public class NativeKeyAccessFactoryModuleTest extends AbstractConfigTest {

    private static final String FACTORY_NAME = NativeKeyAccessFactoryModuleFactory.NAME;
    private static final String INSTANCE_NAME = "native-key-access-factory-instance";

    @Before
    public void setUp() throws Exception {
        super.initConfigTransactionManagerImpl(new HardcodedModuleFactoriesResolver(
                this.mockedContext, new NativeKeyAccessFactoryModuleFactory()));
    }

    @Test
    public void testCreateBean() throws Exception {
        NativeTestSupport.assumeSupportedPlatform();
        final CommitStatus status = createInstance();
        assertBeanCount(1, FACTORY_NAME);
        assertStatus(status, 1, 0, 0);
    }

    @Test
    public void testReusingOldInstance() throws Exception {
        NativeTestSupport.assumeSupportedPlatform();
        createInstance();
        final ConfigTransactionJMXClient transaction = this.configRegistryClient
                .createTransaction();
        assertBeanCount(1, FACTORY_NAME);
        final CommitStatus status = transaction.commit();
        assertBeanCount(1, FACTORY_NAME);
        assertStatus(status, 0, 0, 1);
    }

    private CommitStatus createInstance() throws ConflictingVersionException,
            ValidationException, InstanceAlreadyExistsException {
        final ConfigTransactionJMXClient transaction = this.configRegistryClient
                .createTransaction();
        transaction.createModule(FACTORY_NAME, INSTANCE_NAME);
        return transaction.commit();
    }
}
