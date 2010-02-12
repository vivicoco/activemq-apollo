/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * his work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.amqp.protocol.types;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Iterator;
import org.apache.activemq.amqp.protocol.marshaller.AmqpEncodingError;
import org.apache.activemq.amqp.protocol.marshaller.AmqpMarshaller;
import org.apache.activemq.amqp.protocol.marshaller.Encoded;
import org.apache.activemq.amqp.protocol.types.IAmqpList;
import org.apache.activemq.util.buffer.Buffer;

/**
 * Represents a advertise available sasl mechanisms
 * <p>
 * Advertises the available SASL mechanisms that may be used for authentication.
 * </p>
 */
public interface AmqpSaslMechanisms extends AmqpList {



    /**
     * options map
     */
    public void setOptions(AmqpMap options);

    /**
     * options map
     */
    public IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>> getOptions();

    /**
     * supported sasl mechanisms
     * <p>
     * A list of the sasl security mechanisms supported by the sending peer. If the sending
     * peer does not require its partner to authenticate with it, this list may be empty or
     * absent. The server mechanisms are ordered in decreasing level of preference.
     * </p>
     */
    public void setSaslServerMechanisms(AmqpList saslServerMechanisms);

    /**
     * supported sasl mechanisms
     * <p>
     * A list of the sasl security mechanisms supported by the sending peer. If the sending
     * peer does not require its partner to authenticate with it, this list may be empty or
     * absent. The server mechanisms are ordered in decreasing level of preference.
     * </p>
     */
    public IAmqpList getSaslServerMechanisms();

    public static class AmqpSaslMechanismsBean implements AmqpSaslMechanisms{

        private AmqpSaslMechanismsBuffer buffer;
        private AmqpSaslMechanismsBean bean = this;
        private AmqpMap options;
        private AmqpList saslServerMechanisms;

        AmqpSaslMechanismsBean() {
        }

        AmqpSaslMechanismsBean(IAmqpList value) {

        for(int i = 0; i < value.getListCount(); i++) {
            set(i, value.get(i));
        }
    }

    AmqpSaslMechanismsBean(AmqpSaslMechanisms.AmqpSaslMechanismsBean other) {
        this.bean = other;
    }

    public final AmqpSaslMechanismsBean copy() {
        return new AmqpSaslMechanisms.AmqpSaslMechanismsBean(bean);
    }

    public final AmqpSaslMechanisms.AmqpSaslMechanismsBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
        if(buffer == null) {
            buffer = new AmqpSaslMechanismsBuffer(marshaller.encode(this));
        }
        return buffer;
    }

    public final void marshal(DataOutput out, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError{
        getBuffer(marshaller).marshal(out, marshaller);
    }


    public final void setOptions(AmqpMap options) {
        copyCheck();
        bean.options = options;
    }

    public final IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>> getOptions() {
        return bean.options.getValue();
    }

    public final void setSaslServerMechanisms(AmqpList saslServerMechanisms) {
        copyCheck();
        bean.saslServerMechanisms = saslServerMechanisms;
    }

    public final IAmqpList getSaslServerMechanisms() {
        return bean.saslServerMechanisms.getValue();
    }

    public void set(int index, AmqpType<?, ?> value) {
        switch(index) {
        case 0: {
            setOptions((AmqpMap) value);
            break;
        }
        case 1: {
            setSaslServerMechanisms((AmqpList) value);
            break;
        }
        default : {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }
        }
    }

    public AmqpType<?, ?> get(int index) {
        switch(index) {
        case 0: {
            return bean.options;
        }
        case 1: {
            return bean.saslServerMechanisms;
        }
        default : {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }
        }
    }

    public int getListCount() {
        return 2;
    }

    public IAmqpList getValue() {
        return bean;
    }

    public Iterator<AmqpType<?, ?>> iterator() {
        return new AmqpListIterator(bean);
    }


    private final void copyCheck() {
        if(buffer != null) {;
            throw new IllegalStateException("unwriteable");
        }
        if(bean != this) {;
            copy(bean);
        }
    }

    private final void copy(AmqpSaslMechanisms.AmqpSaslMechanismsBean other) {
        bean = this;
    }

    public boolean equals(Object o){
        if(this == o) {
            return true;
        }

        if(o == null || !(o instanceof AmqpSaslMechanisms)) {
            return false;
        }

        return equals((AmqpSaslMechanisms) o);
    }

    public boolean equals(AmqpSaslMechanisms b) {

        if(b.getOptions() == null ^ getOptions() == null) {
            return false;
        }
        if(b.getOptions() != null && !b.getOptions().equals(getOptions())){ 
            return false;
        }

        if(b.getSaslServerMechanisms() == null ^ getSaslServerMechanisms() == null) {
            return false;
        }
        if(b.getSaslServerMechanisms() != null && !b.getSaslServerMechanisms().equals(getSaslServerMechanisms())){ 
            return false;
        }
        return true;
    }

    public int hashCode() {
        return AbstractAmqpList.hashCodeFor(this);
    }
}

    public static class AmqpSaslMechanismsBuffer extends AmqpList.AmqpListBuffer implements AmqpSaslMechanisms{

        private AmqpSaslMechanismsBean bean;

        protected AmqpSaslMechanismsBuffer(Encoded<IAmqpList> encoded) {
            super(encoded);
        }

        public final void setOptions(AmqpMap options) {
            bean().setOptions(options);
        }

        public final IAmqpMap<AmqpType<?, ?>, AmqpType<?, ?>> getOptions() {
            return bean().getOptions();
        }

        public final void setSaslServerMechanisms(AmqpList saslServerMechanisms) {
            bean().setSaslServerMechanisms(saslServerMechanisms);
        }

        public final IAmqpList getSaslServerMechanisms() {
            return bean().getSaslServerMechanisms();
        }

        public void set(int index, AmqpType<?, ?> value) {
            bean().set(index, value);
        }

        public AmqpType<?, ?> get(int index) {
            return bean().get(index);
        }

        public int getListCount() {
            return bean().getListCount();
        }

        public Iterator<AmqpType<?, ?>> iterator() {
            return bean().iterator();
        }

        public IAmqpList getValue() {
            return bean().getValue();
        }

        public AmqpSaslMechanisms.AmqpSaslMechanismsBuffer getBuffer(AmqpMarshaller marshaller) throws AmqpEncodingError{
            return this;
        }

        protected AmqpSaslMechanisms bean() {
            if(bean == null) {
                bean = new AmqpSaslMechanisms.AmqpSaslMechanismsBean(encoded.getValue());
                bean.buffer = this;
            }
            return bean;
        }

        public boolean equals(Object o){
            return bean().equals(o);
        }

        public boolean equals(AmqpSaslMechanisms o){
            return bean().equals(o);
        }

        public int hashCode() {
            return bean().hashCode();
        }

        public static AmqpSaslMechanisms.AmqpSaslMechanismsBuffer create(Encoded<IAmqpList> encoded) {
            if(encoded.isNull()) {
                return null;
            }
            return new AmqpSaslMechanisms.AmqpSaslMechanismsBuffer(encoded);
        }

        public static AmqpSaslMechanisms.AmqpSaslMechanismsBuffer create(DataInput in, AmqpMarshaller marshaller) throws IOException, AmqpEncodingError {
            return create(marshaller.unmarshalAmqpSaslMechanisms(in));
        }

        public static AmqpSaslMechanisms.AmqpSaslMechanismsBuffer create(Buffer buffer, int offset, AmqpMarshaller marshaller) throws AmqpEncodingError {
            return create(marshaller.decodeAmqpSaslMechanisms(buffer, offset));
        }
    }
}