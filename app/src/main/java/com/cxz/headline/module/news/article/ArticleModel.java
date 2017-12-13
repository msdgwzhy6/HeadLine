package com.cxz.headline.module.news.article;

import com.cxz.headline.base.mvp.BaseModel;
import com.cxz.headline.bean.news.NewsMultiArticleBean;
import com.cxz.headline.di.scope.FragmentScope;
import com.cxz.headline.http.RetrofitHelper;
import com.cxz.headline.http.cache.NewsCacheProvider;
import com.cxz.headline.http.service.NewsService;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.EvictDynamicKey;
import io.rx_cache2.Reply;

/**
 * Created by chenxz on 2017/12/10.
 */
@FragmentScope
public class ArticleModel extends BaseModel implements ArticleContract.Model {

    private RetrofitHelper mRetrofitHelper;

    @Inject
    public ArticleModel() {
        mRetrofitHelper = RetrofitHelper.getInstance();
    }

    @Override
    public Observable<NewsMultiArticleBean> loadNewsArticleList(final String category, final String minBehotTime) {
        return Observable.just(mRetrofitHelper.obtainRetrofitService(NewsService.class).getNewsArticleList(category, minBehotTime))
                .flatMap(new Function<Observable<NewsMultiArticleBean>, ObservableSource<NewsMultiArticleBean>>() {
                    @Override
                    public ObservableSource<NewsMultiArticleBean> apply(Observable<NewsMultiArticleBean> observable) throws Exception {
                        return mRetrofitHelper.obtainCacheService(NewsCacheProvider.class)
                                .getNewsArticleList(observable, new DynamicKey(minBehotTime), new EvictDynamicKey(true))
                                .map(new Function<Reply<NewsMultiArticleBean>, NewsMultiArticleBean>() {
                                    @Override
                                    public NewsMultiArticleBean apply(Reply<NewsMultiArticleBean> newsMultiArticleBeanReply) throws Exception {
                                        return newsMultiArticleBeanReply.getData();
                                    }
                                });
                    }
                });
//        return Flowable.just(mRetrofitHelper.obtainRetrofitService(NewsService.class).getNewsArticleList(category, minBehotTime))
//                .flatMap(new Function<Flowable<NewsMultiArticleBean>, Publisher<NewsMultiArticleBean>>() {
//                    @Override
//                    public Publisher<NewsMultiArticleBean> apply(Flowable<NewsMultiArticleBean> flowable) throws Exception {
//                        return mRetrofitHelper.obtainCacheService(NewsCacheProvider.class)
//                                .getNewsArticleList(flowable, new DynamicKey(minBehotTime), new EvictDynamicKey(true))
//                                .map(new Function<Reply<NewsMultiArticleBean>, NewsMultiArticleBean>() {
//                                    @Override
//                                    public NewsMultiArticleBean apply(Reply<NewsMultiArticleBean> newsMultiChannelBeanReply) throws Exception {
//                                        return newsMultiChannelBeanReply.getData();
//                                    }
//                                });
//                    }
//                });
    }
}
