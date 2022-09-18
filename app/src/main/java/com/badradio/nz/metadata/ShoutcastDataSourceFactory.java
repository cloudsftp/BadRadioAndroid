package com.badradio.nz.metadata;

import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.TransferListener;

import okhttp3.CacheControl;
import okhttp3.Call;

public final class ShoutcastDataSourceFactory extends HttpDataSource.BaseFactory {

    private final Call.Factory callFactory;
    private final String userAgent;
    private final TransferListener<? super DataSource> transferListener;
    private final ShoutcastMetadataListener shoutcastMetadataListener;
    private final CacheControl cacheControl;

    public ShoutcastDataSourceFactory(Call.Factory callFactory, String userAgent,
                                      TransferListener<? super DataSource> transferListener,
                                      ShoutcastMetadataListener shoutcastMetadataListener) {
        this(callFactory, userAgent, transferListener, shoutcastMetadataListener, null);
    }

    private ShoutcastDataSourceFactory(Call.Factory callFactory, String userAgent,
                                       TransferListener<? super DataSource> transferListener,
                                       ShoutcastMetadataListener shoutcastMetadataListener, CacheControl cacheControl) {
        this.callFactory = callFactory;
        this.userAgent = userAgent;
        this.transferListener = transferListener;
        this.shoutcastMetadataListener = shoutcastMetadataListener;
        this.cacheControl = cacheControl;
    }

    @Override
    protected HttpDataSource createDataSourceInternal(HttpDataSource.RequestProperties requestProperties) {
        return new ShoutcastDataSource(callFactory, userAgent, null, transferListener, shoutcastMetadataListener, cacheControl);
    }

}

