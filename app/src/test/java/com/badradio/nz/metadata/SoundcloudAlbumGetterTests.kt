package com.badradio.nz.metadata

import org.junit.Test

class SoundcloudAlbumGetterTests {


    @Test
    fun testGetSongURLFromSearchResult() {
        val url = SoundcloudAlbumArtGetter.getSongURLFromSearchResult(
            "come and see", "cassyb, north posse",
            comeAndSeeSearchResult
        )
        assert(url == "https://soundcloud.com/xxcassyb/come-and-see") {
            println("was $url")
        }
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

}