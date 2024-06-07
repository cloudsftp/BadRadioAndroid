#!/bin/bash

function check_api_response() {
    url="$1"
    file_name="$2"
    temp_file=/tmp/$file_name

    wget "$url" > $temp_file

    diff $temp_file $test_data_file
}

check_api_response "https://bandcamp.com/search?q=come%20and%20see%20cassyb&item_type=t" bandcamp_search_response.html
check_api_response "https://cassyb.bandcamp.com/track/come-and-see-w-north-posse" bandcamp_song_page.html

check_api_response "https://itunes.apple.com/search?term=cassyb+come+and+see&media=music&limit=1" itunes_search_response.html

check_api_response "https://soundcloud.com/search/sounds?q=come%20and%20see%20cassyb" soundcloud_search_response.html
check_api_response "" soundcloud_song_page.html

