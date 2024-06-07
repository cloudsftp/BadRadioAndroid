#!/bin/bash

function download_file() {
    url="$1"
    file_name="$2"
    temp_file=/tmp/$file_name

    wget "$url" > $temp_file

    diff $temp_file $test_data_file
}

download_file "https://bandcamp.com/search?q=come%20and%20see%20cassyb&item_type=t" bandcamp_search_response.html
download_file "https://cassyb.bandcamp.com/track/come-and-see-w-north-posse" bandcamp_song_page.html

