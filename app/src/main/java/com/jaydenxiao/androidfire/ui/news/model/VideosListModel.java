package com.jaydenxiao.androidfire.ui.news.model;

import com.jaydenxiao.androidfire.api.Api;
import com.jaydenxiao.androidfire.api.HostType;
import com.jaydenxiao.androidfire.bean.VideoData;
import com.jaydenxiao.androidfire.ui.news.contract.VideosListContract;
import com.jaydenxiao.common.baserx.RxSchedulers;
import com.jaydenxiao.common.commonutils.TimeUtil;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * des:视频列表model
 * Created by xsf
 * on 2016.09.14:54
 */
public class VideosListModel implements VideosListContract.Model {

    @Override
    public Observable<List<VideoData>> getVideosListData(final String type, int startPage) {
        return Api.getDefault(HostType.NETEASE_NEWS_VIDEO).getVideoList(Api.getCacheControl(),type,startPage)
                .flatMap(new Function<Map<String, List<VideoData>>, Observable<VideoData>>() {
                    @Override
                    public Observable<VideoData> apply(Map<String, List<VideoData>> map) {
//                        return Observable.from(map.get(type));

                        List<VideoData> VideoDataList = map.get(type);
                        Observable<VideoData> observable = Observable.fromArray(VideoDataList.toArray(new VideoData[VideoDataList.size()]));
                        return observable;
                    }
                })
                //转化时间
                .map(new Function<VideoData, VideoData>() {
                    @Override
                    public VideoData apply(VideoData videoData) {
                        String ptime = TimeUtil.formatDate(videoData.getPtime());
                        videoData.setPtime(ptime);
                        return videoData;
                    }
                })
                .distinct()//去重
//                .toSortedList(new Func2<VideoData, VideoData, Integer>() {
//                    @Override
//                    public Integer call(VideoData videoData, VideoData videoData2) {
//                        return videoData2.getPtime().compareTo(videoData.getPtime());
//                    }
//                })
                .toSortedList(new Comparator<VideoData>() {
                    @Override
                    public int compare(VideoData videoData, VideoData videoData2) {
                        return videoData2.getPtime().compareTo(videoData.getPtime());
                    }
                }).toObservable()
                //声明线程调度
                .compose(RxSchedulers.<List<VideoData>>io_main());
    }
}
