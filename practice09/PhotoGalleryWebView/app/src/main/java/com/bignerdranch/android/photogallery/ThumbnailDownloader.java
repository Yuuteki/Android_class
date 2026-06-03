package com.bignerdranch.android.photogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * ThumbnailDownloader 是一个专门用于下载缩略图的后台线程类。
 *
 * <T> 是泛型，表示下载目标对象的类型。
 * 例如 T 可以是 RecyclerView.ViewHolder，也可以是 ImageView，或者其他对象。
 *
 * 它继承自 HandlerThread，说明它本身就是一个带有 Looper 的后台线程。
 */
public class ThumbnailDownloader<T> extends HandlerThread {

    // 日志标签，用于 Log 输出
    private static final String TAG = "ThumbnailDownloader";

    // 消息类型：表示下载图片的消息
    private static final int MESSAGE_DOWNLOAD = 0;

    /**
     * 后台线程中的 Handler。
     *
     * 它负责接收下载请求消息。
     * 当收到 MESSAGE_DOWNLOAD 消息时，就在后台线程中执行图片下载任务。
     */
    private Handler mRequestHandler;

    /**
     * 保存 target 和 url 的映射关系。
     *
     * key: target，表示图片最终要绑定到哪个对象上。
     * value: String，表示图片的 URL 地址。
     *
     * 使用 ConcurrentHashMap 是因为这个 map 可能会被多个线程同时访问：
     * 1. 主线程调用 queueThumbnail()
     * 2. 后台线程调用 handleRequest()
     * 3. 主线程回调时也会访问它
     */
    private ConcurrentMap<T, String> mRequestMap = new ConcurrentHashMap<>();

    /**
     * 主线程中的 Handler。
     *
     * 它通常是在 Fragment 或 Activity 中创建并传进来的。
     * 作用是：下载完成后，把结果切回主线程处理。
     *
     * 因为 Android 中 UI 更新必须在主线程中执行。
     */
    private Handler mResponseHandler;

    /**
     * 图片下载完成后的监听器。
     *
     * 它是一个接口类型变量。
     * 外部可以通过 setThumbnailDownloadListener() 传入具体实现。
     *
     * 当图片下载完成后，会调用它的 onThumbnailDownloaded() 方法。
     */
    private ThumbnailDownloadListener<T> mThumbnailDownloadListener;

    /**
     * 定义一个回调接口。
     *
     * 外部类需要实现这个接口，用来接收下载完成的图片。
     *
     * @param <T> target 的类型
     */
    public interface ThumbnailDownloadListener<T> {

        /**
         * 图片下载完成后调用的方法。
         *
         * @param target 下载目标对象，比如 ViewHolder
         * @param bitmap 下载并解码出来的图片
         */
        void onThumbnailDownloaded(T target, Bitmap bitmap);
    }

    /**
     * 设置图片下载完成后的监听器。
     *
     * 外部通过这个方法传入一个监听器。
     * 以后图片下载完成后，就会调用这个监听器。
     *
     * @param listener 外部传入的监听器对象
     */
    public void setThumbnailDownloadListener(ThumbnailDownloadListener<T> listener) {
        mThumbnailDownloadListener = listener;
    }

    /**
     * 构造方法。
     *
     * @param responseHandler 主线程中的 Handler，用于把下载结果切回主线程
     */
    public ThumbnailDownloader(Handler responseHandler) {

        // 调用父类 HandlerThread 的构造方法，给线程设置名字
        super(TAG);

        // 保存主线程 Handler
        mResponseHandler = responseHandler;
    }

    /**
     * 当 HandlerThread 的 Looper 准备好以后，会自动调用这个方法。
     *
     * 注意：
     * HandlerThread 启动后，会创建一个后台线程 Looper。
     * 只有 Looper 准备好之后，才能创建与这个 Looper 绑定的 Handler。
     */
    @Override
    protected void onLooperPrepared() {

        /**
         * 创建后台线程 Handler。
         *
         * 因为这里是在 onLooperPrepared() 中创建的，
         * 所以这个 Handler 绑定的是 ThumbnailDownloader 这个后台线程的 Looper。
         */
        mRequestHandler = new Handler() {

            /**
             * 处理发送到后台线程的消息。
             *
             * @param msg 接收到的消息对象
             */
            @Override
            public void handleMessage(Message msg) {

                // 如果消息类型是下载图片
                if (msg.what == MESSAGE_DOWNLOAD) {

                    /**
                     * 从消息中取出 target。
                     *
                     * msg.obj 是 Object 类型，所以需要强制转换成 T。
                     */
                    T target = (T) msg.obj;

                    // 打印当前要下载的 URL
                    Log.i(TAG, "Got a request for URL: " + mRequestMap.get(target));

                    // 执行真正的图片下载任务
                    handleRequest(target);
                }
            }
        };
    }

    /**
     * 把一个图片下载任务加入队列。
     *
     * 这个方法通常在主线程中调用，比如 RecyclerView 绑定 ViewHolder 时调用。
     *
     * @param target 图片下载完成后要绑定到的目标对象
     * @param url 图片地址
     */
    public void queueThumbnail(T target, String url) {

        // 打印收到的 URL
        Log.i(TAG, "Got a URL: " + url);

        /**
         * 如果 url 是 null，说明这个 target 当前不需要下载图片。
         * 那么就从请求表中移除这个 target。
         */
        if (url == null) {
            mRequestMap.remove(target);
        } else {

            /**
             * 保存 target 和 url 的对应关系。
             *
             * 例如：
             * target = 某个 ViewHolder
             * url = 这个 ViewHolder 要显示的图片地址
             */
            mRequestMap.put(target, url);

            /**
             * 向后台线程发送一条下载消息。
             *
             * obtainMessage(MESSAGE_DOWNLOAD, target)：
             * 创建一条消息，消息类型是 MESSAGE_DOWNLOAD，
             * 消息携带的数据是 target。
             *
             * sendToTarget()：
             * 把消息发送给 mRequestHandler。
             *
             * 因为 mRequestHandler 绑定的是后台线程，
             * 所以后面的 handleMessage() 会在后台线程中执行。
             */
            mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, target)
                    .sendToTarget();
        }
    }

    /**
     * 清空下载队列。
     *
     * 通常在 Fragment 或 Activity 停止时调用，
     * 避免继续处理已经不需要的下载任务。
     */
    public void clearQueue() {

        /**
         * 移除所有类型为 MESSAGE_DOWNLOAD 的消息。
         *
         * 注意：
         * 这只能移除还没有执行的消息。
         * 如果某个下载任务已经开始执行，就不能通过这个方法停止。
         */
        mRequestHandler.removeMessages(MESSAGE_DOWNLOAD);
    }

    /**
     * 真正执行图片下载的方法。
     *
     * 这个方法在后台线程中执行。
     *
     * @param target 下载目标对象
     */
    private void handleRequest(final T target) {
        try {

            /**
             * 根据 target 从请求表中取出对应的 url。
             */
            final String url = mRequestMap.get(target);

            /**
             * 如果 url 为 null，说明这个 target 的请求已经被取消。
             * 直接返回，不再下载。
             */
            if (url == null) {
                return;
            }

            /**
             * 使用 FlickrFetchr 下载图片的字节数据。
             *
             * getUrlBytes(url) 会通过网络请求读取图片数据。
             * 这一步必须放在后台线程中执行，不能放在主线程中。
             */
            byte[] bitmapBytes = new FlickrFetchr().getUrlBytes(url);

            /**
             * 把下载得到的 byte[] 解码成 Bitmap。
             *
             * Bitmap 是 Android 中表示图片的对象。
             */
            final Bitmap bitmap = BitmapFactory
                    .decodeByteArray(bitmapBytes, 0, bitmapBytes.length);

            // 打印日志，说明 Bitmap 创建成功
            Log.i(TAG, "Bitmap created");

            /**
             * 下载完成后，把结果发送回主线程。
             */
            mResponseHandler.post(new Runnable() {

                public void run() {

                    /**
                     * 判断当前 target 对应的 url 是否还是原来的 url。
                     */
                    if (mRequestMap.get(target) != url) {
                        return;
                    }

                    /**
                     * 下载完成后，从请求表中移除这个 target。
                     */
                    mRequestMap.remove(target);

                    /**
                     * 调用监听器，把下载完成的 bitmap 交给外部处理。
                     *
                     * 外部通常会在这里把 bitmap 设置到 ImageView 上。
                     */
                    mThumbnailDownloadListener.onThumbnailDownloaded(target, bitmap);
                }
            });
        } catch (IOException ioe) {

            /**
             * 如果下载过程中出现网络错误或 IO 错误，就打印错误日志。
             */
            Log.e(TAG, "Error downloading image", ioe);
        }
    }
}