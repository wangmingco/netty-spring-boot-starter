package co.wangming.nsb.constant;

/**
 * Created By WangMing On 2019-12-08
 **/
public enum MessageType {

    PROTOBUF(1);

    private int type;

    MessageType(int type) {
        this.type = 1;
    }

    public int getType() {
        return type;
    }

    public static MessageType get(int type) {
        for (MessageType value : values()) {
            if (value.type == type) {
                return value;
            }
        }

        return null;
    }
}
