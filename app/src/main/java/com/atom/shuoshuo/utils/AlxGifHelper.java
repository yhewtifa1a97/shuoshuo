package com.atom.shuoshuo.utils;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.atom.shuoshuo.R;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifIOException;
import pl.droidsonroids.gif.GifImageView;

/**
 * ============================================================
 * project: shuoshuo
 * package: com.atom.shuoshuo.utils
 * fileDescribe:
 * user: admin
 * email: 1299854942@qq.com
 * createTime: 2017/8/1  14:15
 * modify:
 * version:: V1.0
 * ============================================================
 **/
public class AlxGifHelper {

    private AlxGifHelper() {
    }

    static class ProgressViews {

        public WeakReference<GifImageView> gifImageViewWeakReference;//gif显示控件
        public WeakReference<ProgressBar> progressWheelWeakReference;//用来装饰的圆形进度条
        public WeakReference<TextView> textViewWeakReference;//用来显示当前进度的文本框

        public ProgressViews(WeakReference<GifImageView> gifImageViewWeakReference, WeakReference<ProgressBar> progressWheelWeakReference, WeakReference<TextView> textViewWeakReference) {
            this.gifImageViewWeakReference = gifImageViewWeakReference;
            this.progressWheelWeakReference = progressWheelWeakReference;
            this.textViewWeakReference = textViewWeakReference;
        }

        public ProgressViews(WeakReference<GifImageView> gifImageViewWeakReference, WeakReference<ProgressBar> progressWheelWeakReference) {
            this.gifImageViewWeakReference = gifImageViewWeakReference;
            this.progressWheelWeakReference = progressWheelWeakReference;
        }
    }

    public static abstract class DownLoadTask {

        protected abstract void onStart();

        protected abstract void onLoading(long total, long current);

        protected abstract void onSuccess(File file);

        protected abstract void onFailure(Throwable e);

        boolean isCanceled;
    }


    /**
     * 防止同一个gif文件建立多个下载线程,url和imageView是一对多的关系,如果一个imageView建立了一次下载，
     * 那么其他请求这个url的imageView不需要重新开启一次新的下载，这几个imageView同时回调为了防止内存泄漏，
     * 这个一对多的关系均使用LRU缓存
     */
    public static ConcurrentHashMap<String, ArrayList<ProgressViews>> memoryCache;


    public static void displayGif(final String url, GifImageView gifView, ProgressBar progressBar, TextView textView) {
        //首先查询一下这个gif是否已被缓存
        String md5Url = MD5Utils.getURLMd5(url);
        String path = gifView.getContext().getExternalCacheDir().getAbsolutePath() + "/" + md5Url;//带.tmp后缀的是没有下载完成的，用于加载第一帧，不带tmp后缀是下载完成的，
        //这样做的目的是为了防止一个图片正在下载的时候，另一个请求相同url的imageView使用未下载完毕的文件显示一半图像
        Log.i("AlexGIF", "gif图片的缓存路径是" + path);
        Log.i("AlexGIF", "URL是" + url);
        final File cacheFile = new File(path);
        if (cacheFile.exists()) {//如果本地已经有了这个gif的缓存
            Log.i("AlexGIF", "本图片有缓存");
            if (displayImage(cacheFile, gifView)) {//如果本地缓存读取失败就重新联网下载
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                return;
            }
        }
        //为了防止activity被finish了但是还有很多gif还没有加载完成，导致activity没有及时被内存回收导致内存泄漏，这里使用弱引用
        final WeakReference<GifImageView> imageViewWait = new WeakReference<GifImageView>(gifView);
        final WeakReference<ProgressBar> progressBarWait = new WeakReference<ProgressBar>(progressBar);
        final WeakReference<TextView> textViewWait = new WeakReference<TextView>(textView);
//        gifView.setImageResource(R.drawable.ic_default_image);//设置没有下载完成前的默认图片
        if (memoryCache != null && memoryCache.get(url) != null) {//如果以前有别的imageView加载过
            Log.i("AlexGIF", "以前有别的ImageView申请加载过该gif" + url);
            //可以借用以前的下载进度，不需要新建一个下载线程了
            memoryCache.get(url).add(new ProgressViews(imageViewWait, progressBarWait, textViewWait));
            return;
        }
        if (memoryCache == null) memoryCache = new ConcurrentHashMap<>();
        if (memoryCache.get(url) == null) memoryCache.put(url, new ArrayList<ProgressViews>());
        //将现在申请加载的这个imageView放到缓存里，防止重复加载
        memoryCache.get(url).add(new ProgressViews(imageViewWait, progressBarWait, textViewWait));


        // 下载图片
        startDownLoad(url, new File(cacheFile.getAbsolutePath() + ".tmp"), new DownLoadTask() {
            @Override
            public void onStart() {
                Log.i("AlexGIF", "下载GIF开始");
                ProgressBar progressBar = progressBarWait.get();
                if (progressBar != null) {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(0);
                }
            }

            @Override
            public void onLoading(long total, long current) {
                int progress = 0;
                //得到要下载文件的大小，是通过http报文的header的Content-Length获得的，如果获取不到就是-1
                if (total > 0) progress = (int) (current * 100 / total);
                ArrayList<ProgressViews> viewses = memoryCache.get(url);
                if (viewses == null) return;
                Log.i("AlexGIF", "该gif的请求数量是" + viewses.size());
                for (ProgressViews vs : viewses) {//遍历所有的进度条，修改同一个url请求的进度显示
                    ProgressBar progressBar = vs.progressWheelWeakReference.get();
                    if (progressBar != null) {
                        progressBar.setProgress(progress);

                        Log.i("AlexGIF", "下载gif的进度是" + progress + "%" + "    现在大小" + current + "   总大小" + total
                                + "  progressBar是否显示:" + (progressBar.getVisibility() == View.VISIBLE ? "是" : "否"));
                        if (total == -1) progressBar.setProgress(20);//如果获取不到大小，就让进度条一直转
                    }
                    //显示第一帧直到全部下载完之后开始动画
//                    getFirstPicOfGIF(new File(cacheFile.getAbsolutePath() + ".tmp"), vs.gifImageViewWeakReference.get());
                }

            }

            public void onSuccess(File file) {
                if (file == null) return;
                String path = file.getAbsolutePath();
                if (path == null || path.length() < 5) return;
                File downloadFile = new File(path);
                File renameFile = new File(path.substring(0, path.length() - 4));
                if (path.endsWith(".tmp")) downloadFile.renameTo(renameFile);//将.tmp后缀去掉
                Log.i("AlexGIF", "下载GIf成功,文件路径是" + path + " 重命名之后是" + renameFile.getAbsolutePath());
                if (memoryCache == null) return;
                ArrayList<ProgressViews> viewArr = memoryCache.get(url);
                if (viewArr == null || viewArr.size() == 0) return;
                for (ProgressViews ws : viewArr) {//遍历所有的进度条和imageView，同时修改所有请求同一个url的进度
                    //显示imageView
                    GifImageView gifImageView = ws.gifImageViewWeakReference.get();
                    if (gifImageView != null)
                        displayImage(renameFile, gifImageView);
                    //修改进度条
                    ProgressBar progressBar = ws.progressWheelWeakReference.get();
                    if (progressBar != null) progressBar.setVisibility(View.INVISIBLE);
                    TextView textView = ws.textViewWeakReference.get();
                    if (textView != null) textView.setVisibility(View.INVISIBLE);
                }
                Log.i("AlexGIF", url + "的imageView已经全部加载完毕，共有" + viewArr.size() + "个");
                memoryCache.remove(url);//这个url的全部关联imageView都已经显示完毕，清除缓存记录
            }

            @Override
            public void onFailure(Throwable e) {
                Log.i("Alex", "下载gif图片出现异常", e);
                ProgressBar progressBar = progressBarWait.get();
                if (progressBar != null) progressBar.setVisibility(View.INVISIBLE);
                TextView textView = textViewWait.get();
                if (textView != null) textView.setVisibility(View.INVISIBLE);
                if (memoryCache != null) memoryCache.remove(url);//下载失败移除所有的弱引用
            }
        });

    }

    /**
     * 通过本地缓存或联网加载一张GIF图片
     *
     * @param url
     * @param gifView
     */
    public static String displayImage(final String url, GifImageView gifView, ProgressBar progressBar, TextView tvProgress) {
        //首先查询一下这个gif是否已被缓存
        String md5Url = MD5Utils.getURLMd5(url);
        final String[] path = {gifView.getContext().getExternalCacheDir().getAbsolutePath() + "/" + md5Url};//带.tmp后缀的是没有下载完成的，用于加载第一帧，不带tmp后缀是下载完成的，
        //这样做的目的是为了防止一个图片正在下载的时候，另一个请求相同url的imageView使用未下载完毕的文件显示一半图像
        Log.i("AlexGIF", "gif图片的缓存路径是" + path[0]);
        final File cacheFile = new File(path[0]);
        if (cacheFile.exists()) {//如果本地已经有了这个gif的缓存
            Log.i("AlexGIF", "本图片有缓存");
            if (displayImage(cacheFile, gifView)) {//如果本地缓存读取失败就重新联网下载
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                if (tvProgress != null) tvProgress.setVisibility(View.GONE);
                return path[0];
            }
        }
        //为了防止activity被finish了但是还有很多gif还没有加载完成，导致activity没有及时被内存回收导致内存泄漏，这里使用弱引用
        final WeakReference<GifImageView> imageViewWait = new WeakReference<GifImageView>(gifView);
        final WeakReference<ProgressBar> progressBarWait = new WeakReference<ProgressBar>(progressBar);
        final WeakReference<TextView> textViewWait = new WeakReference<TextView>(tvProgress);
        gifView.setImageResource(R.drawable.ic_image_black_24dp);//设置没有下载完成前的默认图片
        if (memoryCache != null && memoryCache.get(url) != null) {//如果以前有别的imageView加载过
            Log.i("AlexGIF", "以前有别的ImageView申请加载过该gif" + url);
            //可以借用以前的下载进度，不需要新建一个下载线程了
            memoryCache.get(url).add(new ProgressViews(imageViewWait, progressBarWait, textViewWait));
            return path[0];
        }
        if (memoryCache == null) memoryCache = new ConcurrentHashMap<>();
        if (memoryCache.get(url) == null) memoryCache.put(url, new ArrayList<ProgressViews>());
        //将现在申请加载的这个imageView放到缓存里，防止重复加载
        memoryCache.get(url).add(new ProgressViews(imageViewWait, progressBarWait, textViewWait));


        // 下载图片
        startDownLoad(url, new File(cacheFile.getAbsolutePath() + ".tmp"), new DownLoadTask() {
            @Override
            public void onStart() {
                Log.i("AlexGIF", "下载GIF开始");
                ProgressBar progressBar = progressBarWait.get();
                TextView tvProgress = textViewWait.get();
                if (progressBar != null) {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(0);
                    if (tvProgress == null) return;
                    tvProgress.setVisibility(View.VISIBLE);
                    tvProgress.setText("1%");
                }
            }

            @Override
            public void onLoading(long total, long current) {
                int progress = 0;
                //得到要下载文件的大小，是通过http报文的header的Content-Length获得的，如果获取不到就是-1
                if (total > 0) progress = (int) (current * 100 / total);
                Log.i("AlexGIF", "下载gif的进度是" + progress + "%" + "    现在大小" + current + "   总大小" + total);
                ArrayList<ProgressViews> viewses = memoryCache.get(url);
                if (viewses == null) return;
                Log.i("AlexGIF", "该gif的请求数量是" + viewses.size());
                for (ProgressViews vs : viewses) {//遍历所有的进度条，修改同一个url请求的进度显示
                    ProgressBar progressBar = vs.progressWheelWeakReference.get();
                    if (progressBar != null) {
                        progressBar.setProgress(progress / 100);
                        if (total == -1) progressBar.setProgress(20);//如果获取不到大小，就让进度条一直转
                    }
                    TextView tvProgress = vs.textViewWeakReference.get();
                    if (tvProgress != null) tvProgress.setText(progress + "%");
                    //显示第一帧直到全部下载完之后开始动画
                    getFirstPicOfGIF(new File(cacheFile.getAbsolutePath() + ".tmp"), vs.gifImageViewWeakReference.get());
                }

            }

            public void onSuccess(File file) {
                if (file == null) return;
                String filepath = file.getAbsolutePath();
                if (filepath == null || filepath.length() < 5) return;
                File downloadFile = new File(filepath);
                File renameFile = new File(filepath.substring(0, filepath.length() - 4));
                if (filepath.endsWith(".tmp")) downloadFile.renameTo(renameFile);//将.tmp后缀去掉
                Log.i("AlexGIF", "下载GIf成功,文件路径是" + filepath + " 重命名之后是" + renameFile.getAbsolutePath());
                if (memoryCache == null) return;
                ArrayList<ProgressViews> viewArr = memoryCache.get(url);
                if (viewArr == null || viewArr.size() == 0) return;
                for (ProgressViews ws : viewArr) {//遍历所有的进度条和imageView，同时修改所有请求同一个url的进度
                    //显示imageView
                    GifImageView gifImageView = ws.gifImageViewWeakReference.get();
                    if (gifImageView != null)
                        displayImage(renameFile, gifImageView);
                    //修改进度条
                    TextView tvProgress = ws.textViewWeakReference.get();
                    ProgressBar progressBar = ws.progressWheelWeakReference.get();
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    if (tvProgress != null) tvProgress.setVisibility(View.GONE);
                }
                Log.i("AlexGIF", url + "的imageView已经全部加载完毕，共有" + viewArr.size() + "个");
                memoryCache.remove(url);//这个url的全部关联imageView都已经显示完毕，清除缓存记录
                path[0] = renameFile.getAbsolutePath();
            }

            @Override
            public void onFailure(Throwable e) {
                Log.i("Alex", "下载gif图片出现异常", e);
                TextView tvProgress = textViewWait.get();
                ProgressBar progressBar = progressBarWait.get();
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                if (tvProgress != null) tvProgress.setText("image download failed");
                if (memoryCache != null) memoryCache.remove(url);//下载失败移除所有的弱引用
                path[0] = null;
            }
        });
        return path[0];
    }

    /**
     * 开启下载任务到线程池里，防止多并发线程过多
     *
     * @param uri
     * @param os
     * @param task
     */
    public static void startDownLoad(final String uri, final File os, final DownLoadTask task) {
        final Handler handler = new Handler();
        new AlxMultiTask<Void, Void, Void>() {//开启一个多线程池，大小为cpu数量+1

            @Override
            protected Void doInBackground(Void... params) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        task.onStart();
                    }
                });
                downloadToStream(uri, os, task, handler);
                return null;
            }
        }.executeDependSDK();
    }


    /**
     * 通过本地文件显示GIF文件
     *
     * @param is           本地的文件指针
     * @param gifImageView displayWidth imageView控件的宽度，用于根据gif的实际高度重设控件的高度来保证完整显示，传0表示不缩放gif的大小，显示原始尺寸
     */
    public static boolean displayImage(File is, GifImageView gifImageView) {
        if (is == null || gifImageView == null) {
            return false;
        }
        Log.i("AlexGIF", "准备加载gif");
        GifDrawable gifFrom;
        try {
            gifFrom = new GifDrawable(is);
            gifImageView.setImageDrawable(gifFrom);
            return true;
        } catch (GifIOException e) {
            Log.i("AlexGIF", "显示gif出现异常", e);
            return false;
        } catch (IOException e) {
            Log.i("AlexGIF", "显示gif出现异常", e);
            return false;
        } catch (Exception e) {
            Log.i("AlexGIF", "显示gif出现异常", e);
            return false;
        }
    }

    /**
     * 加载gif的第一帧图像，用于下载完成前占位
     *
     * @param gifFile
     * @param imageView
     */
    public static void getFirstPicOfGIF(File gifFile, GifImageView imageView) {
        if (imageView == null) return;
        if (imageView.getTag(R.style.AppTheme) instanceof Integer) return;//之前已经显示过第一帧了，就不用再显示了
        try {
            GifDrawable gifFromFile = new GifDrawable(gifFile);
            boolean canSeekForward = gifFromFile.canSeekForward();
            if (!canSeekForward) return;
            Log.i("AlexGIF", "是否能显示第一帧图片" + canSeekForward);
            //下面是一些其他有用的信息
//            int frames = gifFromFile.getNumberOfFrames();
//            Log.i("AlexGIF","已经下载完多少帧"+frames);
//            int bytecount = gifFromFile.getFrameByteCount();
//            Log.i("AlexGIF","一帧至少多少字节"+bytecount);
//            long memoryCost = gifFromFile.getAllocationByteCount();
//            Log.i("AlexGIF","内存开销是"+memoryCost);
            gifFromFile.seekToFrame(0);
            gifFromFile.pause();//静止在该帧
            imageView.setImageDrawable(gifFromFile);
            imageView.setTag(R.style.AppTheme, 1);//标记该imageView已经显示过第一帧了
        } catch (IOException e) {
            Log.i("AlexGIF", "获取gif信息出现异常", e);
        }
    }

    public static void saveGIF(final View view, final String url, final ProgressBar mProgressBar) {
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        File directory = new File(externalStorageDirectory, view.getContext().getString(R.string.app_name));
        File directoryGifCache = new File(directory, "gifCache");
        if (!directoryGifCache.exists())
            directoryGifCache.mkdirs();

        File file = new File(directoryGifCache, MD5Utils.getURLMd5(url) + ".gif");

        AlxGifHelper.startDownLoad(url, file, new AlxGifHelper.DownLoadTask() {
            @Override
            public void onStart() {
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setProgress(0);
                Toast.makeText(view.getContext(), "正在保存...", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onLoading(long total, long current) {
                int progress = 0;
                //得到要下载文件的大小，是通过http报文的header的Content-Length获得的，如果获取不到就是-1
                if (total > 0) progress = (int) (current * 100 / total);
                mProgressBar.setProgress(progress);
            }

            @Override
            protected void onSuccess(File target) {
                mProgressBar.setVisibility(View.INVISIBLE);
                // 通知图库刷新
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri uri = Uri.fromFile(target);
                intent.setData(uri);
                view.getContext().sendBroadcast(intent);
                SnackbarUtil.showMessage(view, "已保存到" + target.getAbsolutePath());
            }

            @Override
            protected void onFailure(Throwable e) {
                Log.e("TAG", "saveImage: " + e.getMessage());
                SnackbarUtil.showMessage(view, "啊偶, 出错了", "再试试", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        saveGIF(view, url, mProgressBar);
                    }
                });
            }
        });
    }


    /**
     * 通过httpconnection下载一个文件，使用普通的IO接口进行读写
     *
     * @param uri
     * @param file
     * @param task
     * @return
     */
    public static long downloadToStream(String uri, final File file, final DownLoadTask task, Handler handler) {

        if (task == null || task.isCanceled) return -1;

        HttpURLConnection httpURLConnection = null;
        BufferedInputStream bis = null;
        OutputStream os = null;
        long result = -1;
        long fileLen = 0;
        long currCount = 0;
        try {

            try {
                final URL url = new URL(uri);
                os = new FileOutputStream(file);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(20000);
                httpURLConnection.setReadTimeout(10000);

                final int responseCode = httpURLConnection.getResponseCode();
                if (HttpURLConnection.HTTP_OK == responseCode) {
                    bis = new BufferedInputStream(httpURLConnection.getInputStream());
                    result = httpURLConnection.getExpiration();
                    result = result < System.currentTimeMillis() ? System.currentTimeMillis() + 40000 : result;
                    fileLen = httpURLConnection.getContentLength();//这里通过http报文的header Content-Length来获取gif的总大小，需要服务器提前把header写好
                } else {
                    Log.e("Alex", "downloadToStream -> responseCode ==> " + responseCode);
                    return -1;
                }
            } catch (final Exception ex) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        task.onFailure(ex);
                    }
                });
                return -1;
            }


            if (task.isCanceled) return -1;

            byte[] buffer = new byte[4096];//每4k更新进度一次
            int len = 0;
            BufferedOutputStream out = new BufferedOutputStream(os);
            while ((len = bis.read(buffer)) != -1) {
                out.write(buffer, 0, len);
                currCount += len;
                if (task.isCanceled) return -1;
                final long finalFileLen = fileLen;
                final long finalCurrCount = currCount;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        task.onLoading(finalFileLen, finalCurrCount);
                    }
                });
            }
            out.flush();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    task.onSuccess(file);
                }
            });
        } catch (final Throwable e) {
            result = -1;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    task.onFailure(e);
                }
            });
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (final Throwable e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            task.onFailure(e);
                        }
                    });
                }
            }
        }
        return result;
    }
}
