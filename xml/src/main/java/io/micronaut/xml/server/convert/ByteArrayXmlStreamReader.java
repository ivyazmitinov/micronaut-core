/*
 * Copyright 2017-2019 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.xml.server.convert;

import com.fasterxml.aalto.stax.InputFactoryImpl;
import io.micronaut.core.annotation.Internal;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.util.StreamReaderDelegate;
import java.util.Arrays;

/**
 * Stream reader that pairs xml stream with underlying byte array.
 *
 * @author sergey.vishnyakov
 */
@Internal
public final class ByteArrayXmlStreamReader extends StreamReaderDelegate {

    private static final InputFactoryImpl XML_STREAM_FACTORY = new InputFactoryImpl();

    private byte[] bytes;

    /**
     * @param bytes raw representation of xml
     * @throws XMLStreamException if byte array represents corrupted xml
     */
    public ByteArrayXmlStreamReader(byte[] bytes) throws XMLStreamException {
        super(toReader(bytes));
        this.bytes = bytes;
    }

    private static XMLStreamReader toReader(byte[] bytes) throws XMLStreamException {
        return XML_STREAM_FACTORY.createAsyncFor(bytes);
    }

    /**
     * @return copy of byte array of the underlying stream.
     */
    byte[] getBytes() {
        return Arrays.copyOf(this.bytes, bytes.length);
    }

    /**
     *
     * @return copy of the given stream
     */
    public XMLStreamReader copy() {
        try {
            return new ByteArrayXmlStreamReader(bytes);
        } catch (XMLStreamException e) {
            // We can get exception only if there are a problem with parsing an xml out of byte array.
            // Taking into we got here, it means we already have instance of the class and byte array was accepted just
            // fine. That is why it is safe to assume that if we use same byte array to create new instance, there should not
            // be an exception unless byte array was modified.
            throw new RuntimeException(e);
        }
    }
}