package com.why.commonconst;

public interface RabbitConst {

    String EXCHANGE_DIRECT_VIDEO = "exchange.video";

    String ROUTING_KEY_VIDEO_DELETE = "video.delete";
    String QUEUE_VIDEO_DELETE = "queue.video.delete";

    String ROUTING_KEY_VIDEO_DELETE_MULTI = "video.delete.multi";
    String QUEUE_VIDEO_DELETE_MULTI = "queue.video.delete.multi";

    String EXCHANGE_ERROR_VIDEO = "exchange.error.direct.video";
    String QUEUE_ERROR_VIDEO = "queue.error.video";
    String ROUTING_KEY_ERROR_VIDEO = "error.video";

    String EXCHANGE_DIRECT_OSS = "exchange.oss";
    String QUEUE_OSS_DELETE = "queue.delete";
    String ROUTING_KEY_OSS_DELETE = "oss.delete";

    String QUEUE_OSS_DELETE_MULTI = "queue.oss.delete.multi";
    String ROUTING_KEY_OSS_DELETE_MULTI = "oss.delete.multi";

    String EXCHANGE_ERROR_OSS = "exchange.error.oss";
    String QUEUE_ERROR_OSS = "queue.error.oss";
    String ROUTING_KEY_ERROR_OSS = "error.oss";

}
