package com.badradio.nz.metadata.art

import com.badradio.nz.metadata.SongMetadata
import org.junit.Test

class SoundcloudAlbumGetterTests {

    @Test
    fun testGetImageURL() {
        val url = SoundcloudAlbumArtGetter.getImageURL(
            SongMetadata("come and see", "cassyb, north posse")
        )
        assert(url == "https://i1.sndcdn.com/artworks-tOmHVP9GnI66ky4d-8ZWV8w-t500x500.jpg") {
            println("was $url")
        }
    }

    @Test
    fun testGetImageURLFromSongPage() {
        val url = SoundcloudAlbumArtGetter.getImageURLFromSongPage(
            comeAndSeeSongPage
        )
        assert(url == "https://i1.sndcdn.com/artworks-tOmHVP9GnI66ky4d-8ZWV8w-t500x500.jpg") {
            println("was $url")
        }
    }

    @Test
    fun getSongURL() {
        val url = SoundcloudAlbumArtGetter.getSongURL(
            SongMetadata("come and see", "cassyb, north posse")
        )
        assert(url == "https://soundcloud.com/xxcassyb/come-and-see") {
            println("was $url")
        }
    }

    @Test
    fun testGetSongURLFromSearchResult() {
        val url = SoundcloudAlbumArtGetter.getSongURLFromSearchResult(
            SongMetadata("come and see", "cassyb, north posse"),
            comeAndSeeSearchResult
        )
        assert(url == "https://soundcloud.com/xxcassyb/come-and-see") {
            println("was $url")
        }
    }

    private fun runSongMatchesMetadata(songTitle: String, metadataTitle: String): Boolean {
        val song = SoundcloudSong(
            0, "track", "some_link", songTitle,
            SoundcloudUser("username")
        )
        val metadata = SongMetadata(metadataTitle, "username")
        return SoundcloudAlbumArtGetter.songMatchesMetadata(song, metadata)
    }

    @Test
    fun testSongMatchesMetadata() {
        assert(runSongMatchesMetadata("testtitle", "testtitle"))
        assert(!runSongMatchesMetadata("wrongtitle", "righttitle"))
        assert(runSongMatchesMetadata("thissong w/ collaborator", "thissong"))
        assert(runSongMatchesMetadata("thissong", "thissong w/ collaborator"))
        assert(runSongMatchesMetadata("thissong", "thissong (feat. collaborator)"))
    }

    private val comeAndSeeSearchResult = """
{
    "collection": [
        {
            "artwork_url": "https://i1.sndcdn.com/artworks-tOmHVP9GnI66ky4d-8ZWV8w-large.jpg",
            "caption": null,
            "commentable": true,
            "comment_count": 61,
            "created_at": "2022-03-09T15:40:47Z",
            "description": "@northposse (forthcoming PARIS ROBBERY 2)\nno war",
            "downloadable": false,
            "download_count": 0,
            "duration": 165042,
            "full_duration": 165042,
            "embeddable_by": "all",
            "genre": "WRLD WAR",
            "has_downloads_left": false,
            "id": 1229209234,
            "kind": "track",
            "label_name": null,
            "last_modified": "2022-05-17T08:53:07Z",
            "license": "all-rights-reserved",
            "likes_count": 711,
            "permalink": "come-and-see",
            "permalink_url": "https://soundcloud.com/xxcassyb/come-and-see",
            "playback_count": 17588,
            "public": true,
            "publisher_metadata": {
                "id": 1229209234,
                "urn": "soundcloud:tracks:1229209234",
                "contains_music": true
            },
            "purchase_title": null,
            "purchase_url": "https://cassyb.bandcamp.com/track/come-and-see-w-north-posse",
            "release_date": null,
            "reposts_count": 107,
            "secret_token": null,
            "sharing": "public",
            "state": "finished",
            "streamable": true,
            "tag_list": "\"CASSYB \" NORTHPOSSE ALWAYSPROPER MEMPHIS RAP TRAP PHONK 2022 WAR",
            "title": "COME AND SEE w/NORTH POSSE",
            "track_format": "single-track",
            "uri": "https://api.soundcloud.com/tracks/1229209234",
            "urn": "soundcloud:tracks:1229209234",
            "user_id": 247218581,
            "visuals": null,
            "waveform_url": "https://wave.sndcdn.com/nMeuErQ8uQ8u_m.json",
            "display_date": "2022-03-09T15:40:47Z",
            "media": {
                "transcodings": [
                    {
                        "url": "https://api-v2.soundcloud.com/media/soundcloud:tracks:1229209234/546fcabd-05bc-44ad-8e83-bc968a6a222a/stream/hls",
                        "preset": "mp3_0_1",
                        "duration": 165042,
                        "snipped": false,
                        "format": {
                            "protocol": "hls",
                            "mime_type": "audio/mpeg"
                        },
                        "quality": "sq"
                    },
                    {
                        "url": "https://api-v2.soundcloud.com/media/soundcloud:tracks:1229209234/546fcabd-05bc-44ad-8e83-bc968a6a222a/stream/progressive",
                        "preset": "mp3_0_1",
                        "duration": 165042,
                        "snipped": false,
                        "format": {
                            "protocol": "progressive",
                            "mime_type": "audio/mpeg"
                        },
                        "quality": "sq"
                    },
                    {
                        "url": "https://api-v2.soundcloud.com/media/soundcloud:tracks:1229209234/0b0b63bb-9bca-426a-a207-20b1fa444211/stream/hls",
                        "preset": "opus_0_0",
                        "duration": 165016,
                        "snipped": false,
                        "format": {
                            "protocol": "hls",
                            "mime_type": "audio/ogg; codecs=\"opus\""
                        },
                        "quality": "sq"
                    }
                ]
            },
            "station_urn": "soundcloud:system-playlists:track-stations:1229209234",
            "station_permalink": "track-stations:1229209234",
            "track_authorization": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJnZW8iOiJERSIsInN1YiI6IiIsInJpZCI6ImRkYTViYWM4LWI2N2MtNDQxMi04NTkzLTUxYWE0MTU1MjNhNyIsImlhdCI6MTY2NDM5MDc5MH0.CG7Q-WpJ4YSs_7bDTi1V061UMkgnFAn_pp-JgNViVwE",
            "monetization_model": "BLACKBOX",
            "policy": "MONETIZE",
            "user": {
                "avatar_url": "https://i1.sndcdn.com/avatars-HZo13mpyEMz47Rb0-t5qgQA-large.jpg",
                "city": "Kharkiv",
                "comments_count": 0,
                "country_code": "UA",
                "created_at": "2016-08-13T20:45:18Z",
                "creator_subscriptions": [
                    {
                        "product": {
                            "id": "free"
                        }
                    }
                ],
                "creator_subscription": {
                    "product": {
                        "id": "free"
                    }
                },
                "description": "listen me on spotify, thank u <3\n https://open.spotify.com/artist/101VkCohruu1nxu5lboDWa?si=s6pyNZFvSOy5_VxTY4m4FQ\n@alwaysprop3r  \n(BUY BEATS WITHOUT LYRICS AND BUSINESS OFFERS WRITE IN DM/PM)",
                "followers_count": 6308,
                "followings_count": 1916,
                "first_name": "iconpills",
                "full_name": "iconpills",
                "groups_count": 0,
                "id": 247218581,
                "kind": "user",
                "last_modified": "2022-05-16T21:17:51Z",
                "last_name": "",
                "likes_count": 2080,
                "playlist_likes_count": 61,
                "permalink": "xxcassyb",
                "permalink_url": "https://soundcloud.com/xxcassyb",
                "playlist_count": 1,
                "reposts_count": null,
                "track_count": 46,
                "uri": "https://api.soundcloud.com/users/247218581",
                "urn": "soundcloud:users:247218581",
                "username": "cassyb",
                "verified": true,
                "visuals": {
                    "urn": "soundcloud:users:247218581",
                    "enabled": true,
                    "visuals": [
                        {
                            "urn": "soundcloud:visuals:166715565",
                            "entry_time": 0,
                            "visual_url": "https://i1.sndcdn.com/visuals-000247218581-ylfQ5A-original.jpg"
                        }
                    ],
                    "tracking": null
                },
                "badges": {
                    "pro": false,
                    "pro_unlimited": false,
                    "verified": true
                },
                "station_urn": "soundcloud:system-playlists:artist-stations:247218581",
                "station_permalink": "artist-stations:247218581"
            }
        }
    ],
    "total_results": 1,
    "query_urn": "soundcloud:search:cdd70674393c4829ba0f10cbccb57b6c"
}
"""

    private val comeAndSeeSongPage = """
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta name="theme-color" content="#333">
<link rel="dns-prefetch" href="//style.sndcdn.com">
<link rel="dns-prefetch" href="//a-v2.sndcdn.com">
<link rel="dns-prefetch" href="//api-v2.soundcloud.com">
<link rel="dns-prefetch" href="//sb.scorecardresearch.com">
<link rel="dns-prefetch" href="//secure.quantserve.com">
<link rel="dns-prefetch" href="//eventlogger.soundcloud.com">
<link rel="dns-prefetch" href="//api.soundcloud.com">
<link rel="dns-prefetch" href="//ssl.google-analytics.com">
<link rel="dns-prefetch" href="//i1.sndcdn.com">
<link rel="dns-prefetch" href="//i2.sndcdn.com">
<link rel="dns-prefetch" href="//i3.sndcdn.com">
<link rel="dns-prefetch" href="//i4.sndcdn.com">
<link rel="dns-prefetch" href="//wis.sndcdn.com">
<link rel="dns-prefetch" href="//va.sndcdn.com">
<link rel="dns-prefetch" href="//pixel.quantserve.com">
<title>Stream COME AND SEE w/NORTH POSSE by cassyb | Listen online for free on SoundCloud</title>
<meta content="record, sounds, share, sound, audio, tracks, music, soundcloud" name="keywords">
<meta name="referrer" content="origin">
<meta name="google-site-verification" content="dY0CigqM8Inubs_hgrYMwk-zGchKwrvJLcvI_G8631Q">
<link crossorigin="use-credentials" rel="manifest" href="/webmanifest.json">
<meta name="viewport" content="width=device-width,minimum-scale=1,maximum-scale=1,user-scalable=no">
<meta content="19507961798" property="fb:app_id">
<meta content="SoundCloud" property="og:site_name">
<meta content="SoundCloud" property="twitter:site">
<meta content="SoundCloud" property="twitter:app:name:iphone">
<meta content="336353151" property="twitter:app:id:iphone">
<meta content="SoundCloud" property="twitter:app:name:ipad">
<meta content="336353151" property="twitter:app:id:ipad">
<meta content="SoundCloud" property="twitter:app:name:googleplay">
<meta content="com.soundcloud.android" property="twitter:app:id:googleplay">
<link href="/sc-opensearch.xml" rel="search" title="SoundCloud" type="application/opensearchdescription+xml">
<meta name="description" content="Stream COME AND SEE w/NORTH POSSE by cassyb on desktop and mobile. Play over 265 million tracks for free on SoundCloud."><meta property="twitter:app:name:iphone" content="SoundCloud"><meta property="twitter:app:id:iphone" content="336353151"><meta property="twitter:app:name:ipad" content="SoundCloud"><meta property="twitter:app:id:ipad" content="336353151"><meta property="twitter:app:name:googleplay" content="SoundCloud"><meta property="twitter:app:id:googleplay" content="com.soundcloud.android"><meta property="twitter:title" content="COME AND SEE w/NORTH POSSE"><meta property="twitter:description" content="@northposse (forthcoming PARIS ROBBERY 2)
no war"><meta property="twitter:card" content="player"><meta property="twitter:player" content="https://w.soundcloud.com/player/?url=https%3A%2F%2Fapi.soundcloud.com%2Ftracks%2F1229209234&amp;auto_play=false&amp;show_artwork=true&amp;visual=true&amp;origin=twitter"><meta property="twitter:url" content="https://soundcloud.com/xxcassyb/come-and-see"><meta property="twitter:player:height" content="400"><meta property="twitter:player:width" content="435"><meta property="twitter:image" content="https://i1.sndcdn.com/artworks-tOmHVP9GnI66ky4d-8ZWV8w-t500x500.jpg"><meta property="twitter:app:url:googleplay" content="soundcloud://sounds:1229209234"><meta property="twitter:app:url:iphone" content="soundcloud://sounds:1229209234"><meta property="twitter:app:url:ipad" content="soundcloud://sounds:1229209234"><meta property="al:ios:app_name" content="SoundCloud"><meta property="al:ios:app_store_id" content="336353151"><meta property="al:android:app_name" content="SoundCloud"><meta property="al:android:package" content="com.soundcloud.android"><meta property="og:type" content="music.song"><meta property="og:url" content="https://soundcloud.com/xxcassyb/come-and-see"><meta property="og:title" content="COME AND SEE w/NORTH POSSE"><meta property="og:image" content="https://i1.sndcdn.com/artworks-tOmHVP9GnI66ky4d-8ZWV8w-t500x500.jpg"><meta property="og:image:width" content="500"><meta property="og:image:height" content="500"><meta property="og:description" content="@northposse (forthcoming PARIS ROBBERY 2)
no war"><meta property="al:web:should_fallback" content="false"><meta property="al:ios:url" content="soundcloud://sounds:1229209234"><meta property="al:android:url" content="soundcloud://sounds:1229209234"><meta property="soundcloud:user" content="https://soundcloud.com/xxcassyb"><meta property="soundcloud:play_count" content="17625"><meta property="soundcloud:download_count" content="0"><meta property="soundcloud:comments_count" content="61"><meta property="soundcloud:like_count" content="712">
<link rel="canonical" href="https://soundcloud.com/xxcassyb/come-and-see"><link rel="alternate" media="only screen and (max-width: 640px)" href="https://m.soundcloud.com/xxcassyb/come-and-see"><link rel="alternate" type="text/xml+oembed" href="https://soundcloud.com/oembed?url=https%3A%2F%2Fsoundcloud.com%2Fxxcassyb%2Fcome-and-see&amp;format=xml"><link rel="alternate" type="text/json+oembed" href="https://soundcloud.com/oembed?url=https%3A%2F%2Fsoundcloud.com%2Fxxcassyb%2Fcome-and-see&amp;format=json"><link rel="author" href="/xxcassyb"><link rel="alternate" href="android-app://com.soundcloud.android/soundcloud/sounds:1229209234"><link rel="alternate" href="ios-app://336353151/soundcloud/sounds:1229209234">
<meta name="application-name" content="SoundCloud">
<meta name="msapplication-tooltip" content="Launch SoundCloud">
<meta name="msapplication-TileImage" content="https://a-v2.sndcdn.com/assets/images/sc-icons/win8-2dc974a18a.png">
<meta name="msapplication-TileColor" content="#ff5500">
<meta name="msapplication-starturl" content="https://soundcloud.com">
<link href="https://a-v2.sndcdn.com/assets/images/sc-icons/favicon-2cadd14bdb.ico" rel="icon">
<link href="https://a-v2.sndcdn.com/assets/images/sc-icons/ios-a62dfc8fe7.png" rel="apple-touch-icon">
<link href="https://a-v2.sndcdn.com/assets/images/sc-icons/fluid-b4e7a64b8b.png" rel="fluid-icon">
<script>!function(){var o,a,r;function e(a){return a.test(o)}o=window.navigator.userAgent.toLowerCase();var i,t,n,s=void 0!==window.opera&&"[object Opera]"===window.opera.toString(),p=o.match(/\sopr\/([0-9]+)\./),d=e(/chrome/),c=e(/webkit/),m=!d&&e(/safari/),w=!s&&e(/msie|trident/),f=!c&&e(/gecko/);i=p?parseInt(p[1],10):(n=o.match(/(opera|chrome|safari|firefox|msie|rv:)\/?\s*(\.?\d+(\.\d+)*)/i))&&(t=o.match(/version\/([.\d]+)/i))?parseInt(t[1],10):n?parseInt(n[2],10):null;var h=e(/mobile|android|iphone|ipod|symbianos|nokia|s60|playbook|playstation/);f&&(r=(a=o.match(/(firefox)\/?\s*(\.?\d+(\.\d+)*)/i))&&a.length>1&&parseInt(a[2],10)>=41),i&&!h&&(d&&!p&&i<49||f&&!p&&!1===r||m&&i<8||w||s&&i<13||p&&i<27)&&(window.__sc_abortApp=!0)}()</script>
<link rel="stylesheet" href="https://style.sndcdn.com/css/interstate-a86f07cf94ae5a496b24.css">

<link rel="stylesheet" href="https://a-v2.sndcdn.com/assets/css/app-0fda059280f150571fc1.css">
</head>
<body class="ot-hide-banner sc-classic">

<div id="app">
<style>.header{width:100%;background:var(--background-surface-color);height:46px}.sc-classic .header{background:#333}.header__logo{background:var(--background-surface-color)}.sc-classic .header__logo{background:#333}body:not(.sc-classic) .header__logoLink{display:flex;flex-direction:column;justify-content:center;align-content:center}.header__logoLink{height:46px;width:48px}.header__logoLink svg{color:var(--primary-color)}.sc-classic .header__logoLink{background:transparent url(https://a-v2.sndcdn.com/assets/images/header/brand-1b72dd8210.svg) no-repeat 0 11px;background-size:49px 22px;display:block;height:46px;width:49px;margin-right:23px}.sc-classic .header__logoLink{background-image:url(https://a-v2.sndcdn.com/assets/images/header/cloud-e365a472bf.png);background-position-x:12px;background-size:48px 22px;width:69px;margin-right:unset}.sc-classic .header__logoLink svg{display:none}.header__logoLink:focus{background-color:rgba(255,72,0,.8);outline:0}#header__loading{margin:13px auto 0;width:16px;background:url(https://a-v2.sndcdn.com/assets/images/loader-dark-45940ae3d4.gif) center no-repeat;background-size:16px 16px}@media (-webkit-min-device-pixel-ratio:2),(min-resolution:192dpi),(min-resolution:2dppx){.sc-classic .header__logoLink{background-image:url(https://a-v2.sndcdn.com/assets/images/header/cloud@2x-e5fba4606d.png)}}</style>
<div role="banner" class="header sc-selection-disabled show fixed g-dark g-z-index-header">
<div class="header__inner l-container l-fullwidth">
<div class="header__left left">
<div class="header__logo left">
<a href="/" title="Home" class="header__logoLink sc-border-box sc-ir">
<svg viewBox="0 0 143 64" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
<path fill="currentColor" transform="translate(-166.000000, -1125.000000)" d="M308.984235,1169.99251 C308.382505,1180.70295 299.444837,1189.03525 288.718543,1188.88554 L240.008437,1188.88554 C237.777524,1188.86472 235.977065,1187.05577 235.966737,1184.82478 L235.966737,1132.37801 C235.894282,1130.53582 236.962478,1128.83883 238.654849,1128.10753 C238.654849,1128.10753 243.135035,1124.99996 252.572022,1124.99996 C258.337036,1124.99309 263.996267,1126.54789 268.948531,1129.49925 C276.76341,1134.09703 282.29495,1141.75821 284.200228,1150.62285 C285.880958,1150.14737 287.620063,1149.90993 289.36674,1149.91746 C294.659738,1149.88414 299.738952,1152.0036 303.438351,1155.78928 C307.13775,1159.57496 309.139562,1164.70168 308.984235,1169.99251 Z M229.885123,1135.69525 C231.353099,1153.48254 232.420718,1169.70654 229.885123,1187.43663 C229.796699,1188.23857 229.119091,1188.84557 228.312292,1188.84557 C227.505494,1188.84557 226.827885,1188.23857 226.739461,1187.43663 C224.375448,1169.85905 225.404938,1153.33003 226.739461,1135.69525 C226.672943,1135.09199 226.957336,1134.50383 227.471487,1134.18133 C227.985639,1133.85884 228.638946,1133.85884 229.153097,1134.18133 C229.667248,1134.50383 229.951641,1135.09199 229.885123,1135.69525 Z M220.028715,1187.4557 C219.904865,1188.26549 219.208361,1188.86356 218.389157,1188.86356 C217.569953,1188.86356 216.87345,1188.26549 216.7496,1187.4557 C214.986145,1172.28686 214.986145,1156.96477 216.7496,1141.79593 C216.840309,1140.9535 217.551388,1140.31488 218.398689,1140.31488 C219.245991,1140.31488 219.95707,1140.9535 220.047779,1141.79593 C222.005153,1156.95333 221.998746,1172.29994 220.028715,1187.4557 Z M210.153241,1140.2517 C211.754669,1156.55195 212.479125,1171.15545 210.134176,1187.41757 C210.134176,1188.29148 209.425728,1188.99993 208.551813,1188.99993 C207.677898,1188.99993 206.969449,1188.29148 206.969449,1187.41757 C204.70076,1171.36516 205.463344,1156.34224 206.969449,1140.2517 C207.05845,1139.43964 207.744425,1138.82474 208.561345,1138.82474 C209.378266,1138.82474 210.06424,1139.43964 210.153241,1140.2517 Z M200.258703,1187.47476 C200.169129,1188.29694 199.474788,1188.91975 198.647742,1188.91975 C197.820697,1188.91975 197.126356,1188.29694 197.036782,1187.47476 C195.216051,1173.32359 195.216051,1158.99744 197.036782,1144.84627 C197.036782,1143.94077 197.770837,1143.20671 198.676339,1143.20671 C199.581842,1143.20671 200.315897,1143.94077 200.315897,1144.84627 C202.251054,1158.99121 202.231809,1173.33507 200.258703,1187.47476 Z M190.383229,1155.50339 C192.880695,1166.56087 191.755882,1176.32196 190.287906,1187.58915 C190.168936,1188.33924 189.522207,1188.89148 188.762737,1188.89148 C188.003266,1188.89148 187.356537,1188.33924 187.237567,1187.58915 C185.903044,1176.47448 184.797296,1166.48462 187.142244,1155.50339 C187.142244,1154.60842 187.867763,1153.8829 188.762737,1153.8829 C189.65771,1153.8829 190.383229,1154.60842 190.383229,1155.50339 Z M180.526821,1153.82571 C182.814575,1165.15009 182.071055,1174.7396 180.469627,1186.10211 C180.27898,1187.7798 177.400223,1187.79886 177.247706,1186.10211 C175.798795,1174.91118 175.112468,1165.0357 177.190512,1153.82571 C177.281785,1152.97315 178.001234,1152.32661 178.858666,1152.32661 C179.716099,1152.32661 180.435548,1152.97315 180.526821,1153.82571 Z M170.575089,1159.31632 C172.977231,1166.82778 172.157452,1172.92846 170.479765,1180.63056 C170.391921,1181.42239 169.722678,1182.02149 168.925999,1182.02149 C168.12932,1182.02149 167.460077,1181.42239 167.372232,1180.63056 C165.923321,1173.08097 165.332318,1166.84684 167.23878,1159.31632 C167.330053,1158.46376 168.049502,1157.81722 168.906934,1157.81722 C169.764367,1157.81722 170.483816,1158.46376 170.575089,1159.31632 Z"></path>
</svg>
SoundCloud
</a>
</div>
</div>
<div id="header__loading" class="sc-hidden"></div>
</div>
</div>
<script>window.setTimeout((function(){if(!window.__sc_abortApp){var e=window.document.getElementById("header__loading");e&&(e.className="")}}),6e3)</script>
<style>.errorPage__inner{width:580px;margin:0 auto;position:relative;padding-top:460px;background:url(https://a-v2.sndcdn.com/assets/images/errors/500-e5a180b7a8.png) no-repeat 50% 80px;text-align:center;transition:all 1s linear}.errorTitle{margin-bottom:10px;font-size:30px}.errorText{line-height:28px;color:#666;font-size:20px}.errorButtons{margin-top:30px}@media (max-width:1280px){.errorPage__inner{background-size:80%}}</style>
<noscript class="errorPage__inner">
<div class="errorPage__inner">
<p class="errorTitle">JavaScript is disabled</p>
<p class="errorText sc-font-light">You need to enable JavaScript to use SoundCloud</p>
<div class="errorButtons">
<a href="http://www.enable-javascript.com/" target="_blank" class="sc-button sc-button-medium">Show me how to enable it</a>
</div>
</div>
</noscript>
<noscript><article itemscope itemtype="http://schema.org/MusicRecording">
  <header>
    <h1 itemprop="name"><a itemprop="url" href="/xxcassyb/come-and-see">COME AND SEE w/NORTH POSSE</a>
by <a href="/xxcassyb">cassyb</a></h1>
published on <time pubdate>2022-03-09T15:40:47Z</time>

      <meta itemprop="duration" content="PT00H02M45S" />
  <meta itemprop="genre" content="WRLD WAR" />

        <meta itemprop="interactionCount" content="UserLikes:712" />
        <meta itemprop="interactionCount" content="UserDownloads:0" />
        <meta itemprop="interactionCount" content="UserComments:61" />

      <div itemscope itemprop="audio" itemtype="http://schema.org/AudioObject"><meta itemprop="embedUrl" content="https://w.soundcloud.com/player/?url&#x3D;https%3A%2F%2Fapi.soundcloud.com%2Ftracks%2F1229209234&amp;auto_play&#x3D;false&amp;show_artwork&#x3D;true&amp;visual&#x3D;true&amp;origin&#x3D;schema.org" /><meta itemprop="height" content="400px" /></div>
      <div itemscope itemprop="byArtist" itemtype="http://schema.org/MusicGroup"><meta itemprop="name" content="cassyb" /><meta itemprop="url" content="/xxcassyb" /></div>
      <div itemscope itemprop="provider" itemtype="http://schema.org/Organization"><meta itemprop="name" content="SoundCloud" /><meta itemprop="image" content="http://developers.soundcloud.com/assets/logo_white-8bf7615eb575eeb114fc65323068e1e4.png" /></div>
  </header>
  <p>
    <img src="https://i1.sndcdn.com/artworks-tOmHVP9GnI66ky4d-8ZWV8w-t500x500.jpg" width="" height="" alt="COME AND SEE w/NORTH POSSE" itemprop="image">
    @northposse (forthcoming PARIS ROBBERY 2)
no war
    <meta itemprop="description" content="@northposse (forthcoming PARIS ROBBERY 2)
no war" />
  </p>
    <dl>
      <dt>Genre</dt>
      <dd><a href="/tags/WRLD WAR">WRLD WAR</a></dd>
    </dl>

    <section class="comments">
      <h2>Comment by <a href="/gdvbsndy">good vibes andy</a></h2>
<p>Goated track 🔥</p>
<time pubdate>2022-09-22T07:04:38Z</time>
      <h2>Comment by <a href="/soap-mane">SOAP</a></h2>
<p>that snare is slappin&#x27; harder than my dads belt😮‍💨</p>
<time pubdate>2022-05-31T12:07:29Z</time>
      <h2>Comment by <a href="/schizoaz">SCHIZO</a></h2>
<p>very nice</p>
<time pubdate>2022-05-26T17:22:00Z</time>
      <h2>Comment by <a href="/nlls">𝔫 / / 𝔰</a></h2>
<p>&lt;3</p>
<time pubdate>2022-05-18T14:16:46Z</time>
      <h2>Comment by <a href="/netoinncog">Kenessey</a></h2>
<p>ethereal</p>
<time pubdate>2022-05-11T07:21:40Z</time>
      <h2>Comment by <a href="/3humanseeds">3𝖍𝖚𝖒𝖆𝖓𝖘𝖊𝖊𝖉𝖘</a></h2>
<p>⛽🔥🔥Hell Yeah....🔥🔥</p>
<time pubdate>2022-05-11T04:22:10Z</time>
      <h2>Comment by <a href="/visionarylasty">LoVePhiltr</a></h2>
<p>this shit fckin slaps, heavy</p>
<time pubdate>2022-05-03T09:48:14Z</time>
      <h2>Comment by <a href="/kiril-krastev">abstrakt</a></h2>
<p>amazing &lt;3 that bass</p>
<time pubdate>2022-04-19T19:38:55Z</time>
      <h2>Comment by <a href="/liquid-smoke-official">Liquid ${'$'}moke🍃</a></h2>
<p>vibezzzz...</p>
<time pubdate>2022-04-17T09:00:31Z</time>
      <h2>Comment by <a href="/liquid-smoke-official">Liquid ${'$'}moke🍃</a></h2>
<p>hard</p>
<time pubdate>2022-04-17T08:58:50Z</time>
      <h2>Comment by <a href="/ryan-celsius">R y a n   C e l s i u s °</a></h2>
<p>✅</p>
<time pubdate>2022-04-11T23:25:08Z</time>
      <h2>Comment by <a href="/troy-fitzgerald-688470998">Troy</a></h2>
<p>Y&#x27;all different bruh 🔥</p>
<time pubdate>2022-04-09T23:35:54Z</time>
      <h2>Comment by <a href="/fun-44713874">DEFIIRICK🇷🇺</a></h2>
<p>Masterpiece performance!</p>
<time pubdate>2022-04-02T06:12:15Z</time>
      <h2>Comment by <a href="/exotherm">EXOTHERM [MUSIC]</a></h2>
<p>yooo sick beats</p>
<time pubdate>2022-04-01T03:59:25Z</time>
      <h2>Comment by <a href="/krvnkk">krvnkk</a></h2>
<p>stay strong!</p>
<time pubdate>2022-03-28T17:56:40Z</time>
      <h2>Comment by <a href="/numb3rs-107243667">Lis-10-er</a></h2>
<p>ayoo</p>
<time pubdate>2022-03-26T13:59:36Z</time>
      <h2>Comment by <a href="/user-451173976">andzbd</a></h2>
<p>💨</p>
<time pubdate>2022-03-17T16:25:17Z</time>
      <h2>Comment by <a href="/svibeats">${'$'}VI</a></h2>
<p>Fire </p>
<time pubdate>2022-03-15T14:44:29Z</time>
      <h2>Comment by <a href="/wavygenesis">WAVYGENESIS</a></h2>
<p>perfection</p>
<time pubdate>2022-03-15T13:01:54Z</time>
      <h2>Comment by <a href="/ryan-hanson-7">RYANHANSONHAHA</a></h2>
<p>I love the art</p>
<time pubdate>2022-03-15T04:42:26Z</time>
      <h2>Comment by <a href="/kim-maui">Kim.Maui</a></h2>
<p>.. you laid the hammer down</p>
<time pubdate>2022-03-14T16:16:14Z</time>
      <h2>Comment by <a href="/666x0705chw4n999">•πYØRØ$5ÇHW4N5${'$'}π•</a></h2>
<p>insta like du 2 w/ NORTH lmfao ayy wafdup Bois </p>
<time pubdate>2022-03-13T23:09:52Z</time>
      <h2>Comment by <a href="/qubquad">qub</a></h2>
<p>ohhhh my</p>
<time pubdate>2022-03-13T10:01:42Z</time>
      <h2>Comment by <a href="/takumane">DJ TAKUMANE</a></h2>
<p>wavy</p>
<time pubdate>2022-03-13T01:37:02Z</time>
      <h2>Comment by <a href="/joshmfsanchez">joshmfsanchezmane 👀 です</a></h2>
<p>itssickmane</p>
<time pubdate>2022-03-13T00:37:01Z</time>
      <h2>Comment by <a href="/tripletix">TRIPLE TIX</a></h2>
<p>❤️</p>
<time pubdate>2022-03-12T15:44:49Z</time>
      <h2>Comment by <a href="/evoxctive">EVOXCTIVE</a></h2>
<p>CHILL SHYT! &#x27;\&#x27;\&#x27;\ 😈</p>
<time pubdate>2022-03-12T01:52:21Z</time>
      <h2>Comment by <a href="/evily">satanist</a></h2>
<p>bless</p>
<time pubdate>2022-03-11T20:58:36Z</time>
      <h2>Comment by <a href="/mysticmobb">MY${'$'}TIC MOBB</a></h2>
<p>🖤🖤🖤</p>
<time pubdate>2022-03-11T16:09:16Z</time>
      <h2>Comment by <a href="/mysticmobb">MY${'$'}TIC MOBB</a></h2>
<p>DAMMMMMMNNNNNNNNNNNNNN</p>
<time pubdate>2022-03-11T16:08:44Z</time>
    </section>

  <footer>
      <a href="https://cassyb.bandcamp.com/track/come-and-see-w-north-posse">Buy COME AND SEE w/NORTH POSSE</a>
      <ul>
  <li><a href="/xxcassyb/come-and-see/likes">Users who like COME AND SEE w/NORTH POSSE</a></li>
  <li><a href="/xxcassyb/come-and-see/reposts">Users who reposted COME AND SEE w/NORTH POSSE</a></li>
  <li><a href="/xxcassyb/come-and-see/sets">Playlists containing COME AND SEE w/NORTH POSSE</a></li>
  <li><a href="/xxcassyb/come-and-see/recommended">More tracks like COME AND SEE w/NORTH POSSE</a></li>
</ul>
    License: all-rights-reserved
  </footer>
</article>
</noscript>
<style>#updateBrowserMessage{width:600px;margin:0 auto;position:relative;padding-top:410px;background:url(https://a-v2.sndcdn.com/assets/images/errors/browser-9cdd4e6df7.png) no-repeat 50% 130px;text-align:center;display:none}#updateBrowserMessage .messageText{line-height:26px;font-size:20px;margin-bottom:5px}#updateBrowserMessage .downloadLinks{margin-top:0}</style>
<div id="updateBrowserMessage">
<p class="messageText sc-text-light sc-text-secondary">
Your current browser isn't compatible with SoundCloud. <br>
Please download one of our supported browsers.
<a href="https://help.soundcloud.com/hc/articles/115003564308-Technical-requirements">Need help?</a>
</p>
<div class="downloadLinks sc-type-h3 sc-text-h3 sc-text-light sc-text-secondary">
<a href="http://google.com/chrome" target="_blank" title="Chrome">Chrome</a>
| <a href="http://firefox.com" target="_blank" title="Firefox">Firefox</a> |
<a href="http://apple.com/safari" target="_blank" title="Safari">Safari</a>
|
<a href="https://www.microsoft.com/edge" target="_blank" title="Edge">Edge</a>
</div>
</div>
<script>window.__sc_abortApp&&(window.document.getElementById("updateBrowserMessage").style.display="block")</script>
<div id="error__timeout" class="errorPage__inner sc-hidden">
<p class="errorTitle sc-type-h1 sc-text-h1">Sorry! Something went wrong</p>
<div class="errorText sc-font-light">
<p>Is your network connection unstable or browser outdated?</p>
</div>
<div class="errorButtons">
<a class="sc-button" href="https://help.soundcloud.com" target="_blank" id="try-again">I need help</a>
</div>
</div>
<script>function displayError(){if(!window.__sc_abortApp){var r=window.document,e=r.getElementById("error__timeout"),o=r.getElementById("header__loading");e&&o&&(e.className="errorPage__inner",o.className="sc-hidden")}}window.setTimeout(displayError,15e3),window.onerror=displayError</script>
<p>
<a href="/popular/searches" title="Popular searches">Popular searches</a>
</p>
</div>
<script crossorigin src="https://a-v2.sndcdn.com/assets/53-aed4402d.js"></script>
<script crossorigin src="https://a-v2.sndcdn.com/assets/51-dc97422c.js"></script>
<script
  async
  src="https://cdn.cookielaw.org/consent/7e62c772-c97a-4d95-8d0a-f99bbeadcf61/otSDKStub.js"
  type="text/javascript"
  charset="UTF-8"
  data-domain-script="7e62c772-c97a-4d95-8d0a-f99bbeadcf61"
></script>
<style>
  body.ot-hide-banner #onetrust-banner-sdk {
    visibility: hidden;
    transform: translateY(110%);
  }
  #onetrust-banner-sdk {
    transition: transform 1s ease 200ms;
    transform: translateY(0%);
  }
  body.ot-hide-banner .onetrust-pc-dark-filter {
    background: initial;
    width: initial;
    height: initial;
    overflow: initial;
    position: initial;
  }
</style>
<script type="text/javascript">
  (function (global) {
    function OptanonWrapper() {
      var activeGroups = (global.OptanonActiveGroups || '').split(',');

      if (Array.isArray(OptanonWrapper.callbacks)) {
        for (var i = 0, max = OptanonWrapper.callbacks.length; i < max; i++) {
          try {
            OptanonWrapper.callbacks[i](activeGroups);
          } catch (e) {}
        }
      }

      OptanonWrapper.isLoaded = true;
    };

    OptanonWrapper.callbacks = [];
    OptanonWrapper.isLoaded = false;

    global.OptanonWrapper = OptanonWrapper;
  }(window));
</script>

<script>window.__sc_version="1664196962"</script>
<script>window.__sc_hydration = [{"hydratable":"anonymousId","data":"233994-411867-816526-9984"},{"hydratable":"features","data":{"features":["mobi_use_onetrust_gb","v2_enable_onetrust_user_id","v2_peace_ukraine_logo_takeover","v2_use_onetrust_tcfv2_eu1","v2_ie11_support_end","v2_use_updated_alert_banner_quota_upsell","v2_use_onetrust_eu2","mobi_enable_onetrust_tcfv2","mobi_use_onetrust_eu1","v2_use_onetrust_user_id_eu2","v2_enable_sourcepoint_tcfv2","mobi_peace_ukraine_logo_takeover","mobi_use_onetrust_tcfv2_eu2","v2_test_feature_toggle","checkout_send_segment_events_to_event_gateway","mobi_use_onetrust_user_id_eu1","mobi_enable_onetrust_user_id","mobi_use_onetrust_user_id_ex_us","mobi_use_onetrust_tcfv2_eu1","v2_post_with_caption","mobi_use_onetrust_eu4","featured_artists_banner","v2_repost_redirect_page","v2_use_onetrust_gb","mobi_use_onetrust_user_id_global","use_onetrust_async","v2_signals_collection","v2_track_level_distro_to_plan_picker","v2_direct_support_link","v2_hq_file_storage_release","mobi_use_onetrust_user_id_eu2","v2_use_onetrust_eu4","v2_stories_onboarding","v2_enable_onetrust","v2_import_playlist_experiment","v2_disable_sidebar_comments_count","v2_use_onetrust_us","checkout_use_recurly_with_paypal","v2_use_onetrust_tcfv2_ex_us","mobi_use_onetrust_eu3","mobi_use_onetrust_elsewhere","v2_use_onetrust_eu3","mobi_use_onetrust_us","v2_oscp_german_tax_fields_support","v2_use_onetrust_user_id_ex_us","v2_use_new_connect","v2_use_onetrust_tcfv2_eu2","v2_send_segment_events_to_event_gateway","v2_use_onetrust_eu1","v2_enable_sourcepoint","v2_repost_with_caption_graphql","mobi_use_onetrust_tcfv2_ex_us","v2_tags_recent_tracks","mobi_use_onetrust_eu2","v2_use_onetrust_elsewhere","v2_webauth_use_local_tracking","mobi_sign_in_experiment","mobi_enable_onetrust","v2_can_see_insights","mobi_trinity","v2_google_one_tap","v2_enable_pwa","mobi_use_hls_hack","v2_stories","v2_use_onetrust_user_id_eu1","v2_use_onetrust_user_id_global","use_recurly_checkout","v2_show_side_by_side_upsell_experience","v2_enable_onetrust_tcfv2","v2_enable_tcfv2_consent_string_cache","mobi_send_segment_events_to_event_gateway","use_on_soundcloud_short_links"]}},{"hydratable":"experiments","data":{}},{"hydratable":"geoip","data":{"country_code":"DE","country_name":"Germany","region":"HE","city":"Frankfurt am Main","postal_code":"60313","latitude":50.1188,"longitude":8.6843}},{"hydratable":"privacySettings","data":{"allows_messages_from_unfollowed_users":false,"analytics_opt_in":true,"communications_opt_in":true,"targeted_advertising_opt_in":false,"legislation":[]}},{"hydratable":"trackingBrowserTabId","data":"362353"},{"hydratable":"user","data":{"avatar_url":"https://i1.sndcdn.com/avatars-HZo13mpyEMz47Rb0-t5qgQA-large.jpg","city":"Kharkiv","comments_count":0,"country_code":"UA","created_at":"2016-08-13T20:45:18Z","creator_subscriptions":[{"product":{"id":"free"}}],"creator_subscription":{"product":{"id":"free"}},"description":"listen me on spotify, thank u \u003c3\n https://open.spotify.com/artist/101VkCohruu1nxu5lboDWa?si=s6pyNZFvSOy5_VxTY4m4FQ\n@alwaysprop3r  \n(BUY BEATS WITHOUT LYRICS AND BUSINESS OFFERS WRITE IN DM/PM)","followers_count":6316,"followings_count":1898,"first_name":"iconpills","full_name":"iconpills","groups_count":0,"id":247218581,"kind":"user","last_modified":"2022-05-16T21:17:51Z","last_name":"","likes_count":2080,"playlist_likes_count":61,"permalink":"xxcassyb","permalink_url":"https://soundcloud.com/xxcassyb","playlist_count":1,"reposts_count":null,"track_count":46,"uri":"https://api.soundcloud.com/users/247218581","urn":"soundcloud:users:247218581","username":"cassyb","verified":true,"visuals":{"urn":"soundcloud:users:247218581","enabled":true,"visuals":[{"urn":"soundcloud:visuals:166715565","entry_time":0,"visual_url":"https://i1.sndcdn.com/visuals-000247218581-ylfQ5A-original.jpg"}],"tracking":null},"badges":{"pro":false,"pro_unlimited":false,"verified":true},"station_urn":"soundcloud:system-playlists:artist-stations:247218581","station_permalink":"artist-stations:247218581","url":"/xxcassyb"}},{"hydratable":"sound","data":{"artwork_url":"https://i1.sndcdn.com/artworks-tOmHVP9GnI66ky4d-8ZWV8w-large.jpg","caption":null,"commentable":true,"comment_count":61,"created_at":"2022-03-09T15:40:47Z","description":"@northposse (forthcoming PARIS ROBBERY 2)\nno war","downloadable":false,"download_count":0,"duration":165042,"full_duration":165042,"embeddable_by":"all","genre":"WRLD WAR","has_downloads_left":false,"id":1229209234,"kind":"track","label_name":null,"last_modified":"2022-05-17T08:53:07Z","license":"all-rights-reserved","likes_count":712,"permalink":"come-and-see","permalink_url":"https://soundcloud.com/xxcassyb/come-and-see","playback_count":17625,"public":true,"publisher_metadata":{"id":1229209234,"urn":"soundcloud:tracks:1229209234","contains_music":true},"purchase_title":null,"purchase_url":"https://cassyb.bandcamp.com/track/come-and-see-w-north-posse","release_date":null,"reposts_count":107,"secret_token":null,"sharing":"public","state":"finished","streamable":true,"tag_list":"\"CASSYB \" NORTHPOSSE ALWAYSPROPER MEMPHIS RAP TRAP PHONK 2022 WAR","title":"COME AND SEE w/NORTH POSSE","track_format":"single-track","uri":"https://api.soundcloud.com/tracks/1229209234","urn":"soundcloud:tracks:1229209234","user_id":247218581,"visuals":null,"waveform_url":"https://wave.sndcdn.com/nMeuErQ8uQ8u_m.json","display_date":"2022-03-09T15:40:47Z","media":{"transcodings":[{"url":"https://api-v2.soundcloud.com/media/soundcloud:tracks:1229209234/546fcabd-05bc-44ad-8e83-bc968a6a222a/stream/hls","preset":"mp3_0_1","duration":165042,"snipped":false,"format":{"protocol":"hls","mime_type":"audio/mpeg"},"quality":"sq"},{"url":"https://api-v2.soundcloud.com/media/soundcloud:tracks:1229209234/546fcabd-05bc-44ad-8e83-bc968a6a222a/stream/progressive","preset":"mp3_0_1","duration":165042,"snipped":false,"format":{"protocol":"progressive","mime_type":"audio/mpeg"},"quality":"sq"},{"url":"https://api-v2.soundcloud.com/media/soundcloud:tracks:1229209234/0b0b63bb-9bca-426a-a207-20b1fa444211/stream/hls","preset":"opus_0_0","duration":165016,"snipped":false,"format":{"protocol":"hls","mime_type":"audio/ogg; codecs=\"opus\""},"quality":"sq"}]},"station_urn":"soundcloud:system-playlists:track-stations:1229209234","station_permalink":"track-stations:1229209234","track_authorization":"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJnZW8iOiJERSIsInN1YiI6IiIsInJpZCI6ImRkYTViYWM4LWI2N2MtNDQxMi04NTkzLTUxYWE0MTU1MjNhNyIsImlhdCI6MTY2NDQ2NDQ4N30.qTl5AnAwJiuXXymQfNZY8snrC1wRkBKTSscAE5Xyqxo","monetization_model":"BLACKBOX","policy":"MONETIZE","user":{"avatar_url":"https://i1.sndcdn.com/avatars-HZo13mpyEMz47Rb0-t5qgQA-large.jpg","city":"Kharkiv","comments_count":0,"country_code":"UA","created_at":"2016-08-13T20:45:18Z","creator_subscriptions":[{"product":{"id":"free"}}],"creator_subscription":{"product":{"id":"free"}},"description":"listen me on spotify, thank u \u003c3\n https://open.spotify.com/artist/101VkCohruu1nxu5lboDWa?si=s6pyNZFvSOy5_VxTY4m4FQ\n@alwaysprop3r  \n(BUY BEATS WITHOUT LYRICS AND BUSINESS OFFERS WRITE IN DM/PM)","followers_count":6316,"followings_count":1898,"first_name":"iconpills","full_name":"iconpills","groups_count":0,"id":247218581,"kind":"user","last_modified":"2022-05-16T21:17:51Z","last_name":"","likes_count":2080,"playlist_likes_count":61,"permalink":"xxcassyb","permalink_url":"https://soundcloud.com/xxcassyb","playlist_count":1,"reposts_count":null,"track_count":46,"uri":"https://api.soundcloud.com/users/247218581","urn":"soundcloud:users:247218581","username":"cassyb","verified":true,"visuals":{"urn":"soundcloud:users:247218581","enabled":true,"visuals":[{"urn":"soundcloud:visuals:166715565","entry_time":0,"visual_url":"https://i1.sndcdn.com/visuals-000247218581-ylfQ5A-original.jpg"}],"tracking":null},"badges":{"pro":false,"pro_unlimited":false,"verified":true},"station_urn":"soundcloud:system-playlists:artist-stations:247218581","station_permalink":"artist-stations:247218581"}}}];</script>



<script src="https://a-v2.sndcdn.com/assets/19-5b8c0fd9.js" crossorigin></script>
<script crossorigin src="https://a-v2.sndcdn.com/assets/52-0b2bbf4b.js"></script>
<script crossorigin src="https://a-v2.sndcdn.com/assets/1-b194ba86.js"></script>
<script crossorigin src="https://a-v2.sndcdn.com/assets/3-b939bbbe.js"></script>
<script crossorigin src="https://a-v2.sndcdn.com/assets/2-0237db41.js"></script>
<script crossorigin src="https://a-v2.sndcdn.com/assets/0-e2dd0493.js"></script>
<script crossorigin src="https://a-v2.sndcdn.com/assets/50-67117cb0.js"></script>
</body>
</html>
"""

}