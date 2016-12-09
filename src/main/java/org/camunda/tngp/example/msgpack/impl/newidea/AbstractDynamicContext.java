package org.camunda.tngp.example.msgpack.impl.newidea;

import java.nio.ByteBuffer;

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.camunda.tngp.list.CompactList;

public abstract class AbstractDynamicContext
{

    protected CompactList context;
    protected UnsafeBuffer cursorView = new UnsafeBuffer(0, 0);
    protected UnsafeBuffer dynamicContextView = new UnsafeBuffer(0, 0);

    protected int dynamicContextSize;
    protected int elementSize;
    protected int staticElementSize;

    protected DirectBuffer emptyElement;

    public AbstractDynamicContext(int capacity, int staticElementSize, int dynamicContextSize)
    {
        this.staticElementSize = staticElementSize;
        this.dynamicContextSize = dynamicContextSize;
        this.elementSize = staticElementSize + dynamicContextSize;
        context = new CompactList(
                elementSize,
                capacity,
                (size) -> ByteBuffer.allocate(size));
        emptyElement = new UnsafeBuffer(new byte[elementSize]);
    }

    public boolean hasElements()
    {
        return context.size() > 0;
    }

    public int size()
    {
        return context.size();
    }

    // cursor operations

    public void moveTo(int element)
    {
        context.wrap(element, cursorView);
    }

    public void moveToLastElement()
    {
        context.wrap(context.size() - 1, cursorView);
    }

    public void appendElement()
    {
        context.add(emptyElement, 0, elementSize);
        moveToLastElement();
    }

    public void removeLastElement()
    {
        context.remove(context.size() - 1);

        if (size() > 0)
        {
            moveToLastElement();
        }
    }

    public MutableDirectBuffer dynamicContext()
    {
        if (dynamicContextSize > 0)
        {
            dynamicContextView.wrap(cursorView, staticElementSize, dynamicContextSize);
        }
        else
        {
            dynamicContextView.wrap(0, 0);
        }
        return dynamicContextView;
    }

    public void clear()
    {
        context.clear();
    }
}
