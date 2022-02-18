import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import org.junit.Test;

/**
 * @author: wangming
 * @date: 2022/2/18
 */
public class ByteBufTest {

    @Test
    public void testSlice() {
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.heapBuffer(1024);

        byte[] bytes = new byte[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        byteBuf.writeBytes(bytes);
        print(byteBuf, "1. write");

        byteBuf.readByte();
        print(byteBuf, "2. read");

        ByteBuf slice = byteBuf.slice(byteBuf.readerIndex(), 5);
        print(byteBuf, "3. byteBuf");
        print(slice, "3. slice");

        byteBuf.readerIndex(byteBuf.readerIndex() + 5);
        print(byteBuf, "4. byteBuf");

        slice.readByte();
        print(slice, "5. slice");
        print(byteBuf, "5. byteBuf");
    }

    private void print(ByteBuf byteBuf, String type) {
        System.out.println(type + " -> readerIndex:" + byteBuf.readerIndex()
                + ", writerIndex:" + byteBuf.writerIndex()
                + ", capacity:" + byteBuf.capacity()
        );
    }
}
