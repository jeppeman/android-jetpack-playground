package com.jeppeman.jetpackplayground.video

const val VIDEOS_RESPONSE_200 = """
{
  "categories": [
    {
      "name": "Movies",
      "videos": [
        {
          "subtitle": "Fusce id nisi turpis. Praesent viverra bibendum semper. Donec tristique, orci sed semper lacinia, quam erat rhoncus massa, non congue tellus est quis tellus. Sed mollis orci venenatis quam scelerisque accumsan. Curabitur a massa sit amet mi accumsan mollis sed et magna. Vivamus sed aliquam risus. Nulla eget dolor in elit facilisis mattis. Ut aliquet luctus lacus. Phasellus nec commodo erat. Praesent tempus id lectus ac scelerisque. Maecenas pretium cursus lectus id volutpat.",
          "sources": [
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
          ],
          "thumb": "images/BigBuckBunny.jpg",
          "image-480x270": "images_480x270/BigBuckBunny.jpg",
          "image-780x1200": "images_780x1200/BigBuckBunny-780x1200.jpg",
          "title": "Big Buck Bunny",
          "studio": "Blender Foundation"
        },
        {
          "subtitle": "Fusce id nisi turpis. Praesent viverra bibendum semper. Donec tristique, orci sed semper lacinia, quam erat rhoncus massa, non congue tellus est quis tellus. Sed mollis orci venenatis quam scelerisque accumsan. Curabitur a massa sit amet mi accumsan mollis sed et magna. Vivamus sed aliquam risus. Nulla eget dolor in elit facilisis mattis. Ut aliquet luctus lacus. Phasellus nec commodo erat. Praesent tempus id lectus ac scelerisque. Maecenas pretium cursus lectus id volutpat.",
          "sources": [
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
          ],
          "thumb": "images/ElephantsDream.jpg",
          "image-480x270": "images_480x270/ElephantsDream.jpg",
          "image-780x1200": "images_780x1200/ElephantsDream-780x1200.jpg",
          "title": "Elephant Dream",
          "studio": "Blender Foundation"
        },
        {
          "subtitle": "Fusce id nisi turpis. Praesent viverra bibendum semper. Donec tristique, orci sed semper lacinia, quam erat rhoncus massa, non congue tellus est quis tellus. Sed mollis orci venenatis quam scelerisque accumsan. Curabitur a massa sit amet mi accumsan mollis sed et magna. Vivamus sed aliquam risus. Nulla eget dolor in elit facilisis mattis. Ut aliquet luctus lacus. Phasellus nec commodo erat. Praesent tempus id lectus ac scelerisque. Maecenas pretium cursus lectus id volutpat.",
          "sources": [
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
          ],
          "thumb": "images/ForBiggerBlazes.jpg",
          "image-480x270": "images_480x270/ForBiggerBlazes.jpg",
          "image-780x1200": "images_780x1200/Blaze-780x1200.jpg",
          "title": "For Bigger Blazes",
          "studio": "Google"
        },
        {
          "subtitle": "Fusce id nisi turpis. Praesent viverra bibendum semper. Donec tristique, orci sed semper lacinia, quam erat rhoncus massa, non congue tellus est quis tellus. Sed mollis orci venenatis quam scelerisque accumsan. Curabitur a massa sit amet mi accumsan mollis sed et magna. Vivamus sed aliquam risus. Nulla eget dolor in elit facilisis mattis. Ut aliquet luctus lacus. Phasellus nec commodo erat. Praesent tempus id lectus ac scelerisque. Maecenas pretium cursus lectus id volutpat.",
          "sources": [
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4"
          ],
          "thumb": "images/ForBiggerEscapes.jpg",
          "image-480x270": "images_480x270/ForBiggerEscapes.jpg",
          "image-780x1200": "images_780x1200/Escape-780x1200.jpg",
          "title": "For Bigger Escape",
          "studio": "Google"
        },
        {
          "subtitle": "Fusce id nisi turpis. Praesent viverra bibendum semper. Donec tristique, orci sed semper lacinia, quam erat rhoncus massa, non congue tellus est quis tellus. Sed mollis orci venenatis quam scelerisque accumsan. Curabitur a massa sit amet mi accumsan mollis sed et magna. Vivamus sed aliquam risus. Nulla eget dolor in elit facilisis mattis. Ut aliquet luctus lacus. Phasellus nec commodo erat. Praesent tempus id lectus ac scelerisque. Maecenas pretium cursus lectus id volutpat.",
          "sources": [
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4"
          ],
          "thumb": "images/ForBiggerFun.jpg",
          "image-480x270": "images_480x270/ForBiggerFun.jpg",
          "image-780x1200": "images_780x1200/Fun-780x1200.jpg",
          "title": "For Bigger Fun",
          "studio": "Google"
        },
        {
          "subtitle": "Fusce id nisi turpis. Praesent viverra bibendum semper. Donec tristique, orci sed semper lacinia, quam erat rhoncus massa, non congue tellus est quis tellus. Sed mollis orci venenatis quam scelerisque accumsan. Curabitur a massa sit amet mi accumsan mollis sed et magna. Vivamus sed aliquam risus. Nulla eget dolor in elit facilisis mattis. Ut aliquet luctus lacus. Phasellus nec commodo erat. Praesent tempus id lectus ac scelerisque. Maecenas pretium cursus lectus id volutpat.",
          "sources": [
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4"
          ],
          "thumb": "images/ForBiggerJoyrides.jpg",
          "image-480x270": "images_480x270/ForBiggerJoyrides.jpg",
          "image-780x1200": "images_780x1200/Joyride-780x1200.jpg",
          "title": "For Bigger Joyrides",
          "studio": "Google"
        },
        {
          "subtitle": "Fusce id nisi turpis. Praesent viverra bibendum semper. Donec tristique, orci sed semper lacinia, quam erat rhoncus massa, non congue tellus est quis tellus. Sed mollis orci venenatis quam scelerisque accumsan. Curabitur a massa sit amet mi accumsan mollis sed et magna. Vivamus sed aliquam risus. Nulla eget dolor in elit facilisis mattis. Ut aliquet luctus lacus. Phasellus nec commodo erat. Praesent tempus id lectus ac scelerisque. Maecenas pretium cursus lectus id volutpat.",
          "sources": [
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4"
          ],
          "thumb": "images/ForBiggerMeltdowns.jpg",
          "image-480x270": "images_480x270/ForBiggerMeltdowns.jpg",
          "image-780x1200": "images_780x1200/Meltdown-780x1200.jpg",
          "title": "For Bigger Meltdowns",
          "studio": "Google"
        },
        {
          "subtitle": "Fusce id nisi turpis. Praesent viverra bibendum semper. Donec tristique, orci sed semper lacinia, quam erat rhoncus massa, non congue tellus est quis tellus. Sed mollis orci venenatis quam scelerisque accumsan. Curabitur a massa sit amet mi accumsan mollis sed et magna. Vivamus sed aliquam risus. Nulla eget dolor in elit facilisis mattis. Ut aliquet luctus lacus. Phasellus nec commodo erat. Praesent tempus id lectus ac scelerisque. Maecenas pretium cursus lectus id volutpat.",
          "sources": [
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4"
          ],
          "thumb": "images/Sintel.jpg",
          "image-480x270": "images_480x270/Sintel.jpg",
          "image-780x1200": "images_780x1200/Sintel-780x1200.jpg",
          "title": "Sintel",
          "studio": "Blender Foundation"
        },
        {
          "subtitle": "Fusce id nisi turpis. Praesent viverra bibendum semper. Donec tristique, orci sed semper lacinia, quam erat rhoncus massa, non congue tellus est quis tellus. Sed mollis orci venenatis quam scelerisque accumsan. Curabitur a massa sit amet mi accumsan mollis sed et magna. Vivamus sed aliquam risus. Nulla eget dolor in elit facilisis mattis. Ut aliquet luctus lacus. Phasellus nec commodo erat. Praesent tempus id lectus ac scelerisque. Maecenas pretium cursus lectus id volutpat.",
          "sources": [
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackOnStreetAndDirt.mp4"
          ],
          "thumb": "images/SubaruOutbackOnStreetAndDirt.jpg",
          "image-480x270": "images_480x270/SubaruOutbackOnStreetAndDirt.jpg",
          "image-780x1200": "images_780x1200/Subaru-780x1200.jpg",
          "title": "Subaru Outback On Street And Dirt",
          "studio": "Garage419"
        },
        {
          "subtitle": "Fusce id nisi turpis. Praesent viverra bibendum semper. Donec tristique, orci sed semper lacinia, quam erat rhoncus massa, non congue tellus est quis tellus. Sed mollis orci venenatis quam scelerisque accumsan. Curabitur a massa sit amet mi accumsan mollis sed et magna. Vivamus sed aliquam risus. Nulla eget dolor in elit facilisis mattis. Ut aliquet luctus lacus. Phasellus nec commodo erat. Praesent tempus id lectus ac scelerisque. Maecenas pretium cursus lectus id volutpat.",
          "sources": [
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4"
          ],
          "thumb": "images/TearsOfSteel.jpg",
          "image-480x270": "images_480x270/TearsOfSteel.jpg",
          "image-780x1200": "images_780x1200/TearsOfSteel-780x1200.jpg",
          "title": "Tears of Steel",
          "studio": "Blender Foundation"
        },
        {
          "subtitle": "Fusce id nisi turpis. Praesent viverra bibendum semper. Donec tristique, orci sed semper lacinia, quam erat rhoncus massa, non congue tellus est quis tellus. Sed mollis orci venenatis quam scelerisque accumsan. Curabitur a massa sit amet mi accumsan mollis sed et magna. Vivamus sed aliquam risus. Nulla eget dolor in elit facilisis mattis. Ut aliquet luctus lacus. Phasellus nec commodo erat. Praesent tempus id lectus ac scelerisque. Maecenas pretium cursus lectus id volutpat.",
          "sources": [
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/VolkswagenGTIReview.mp4"
          ],
          "thumb": "images/VolkswagenGTIReview.jpg",
          "image-480x270": "images_480x270/VolkswagenGTIReview.jpg",
          "image-780x1200": "images_780x1200/VolksWagen-780x1200.jpg",
          "title": "Volkswagen GTI Review",
          "studio": "Garage419"
        },
        {
          "subtitle": "Fusce id nisi turpis. Praesent viverra bibendum semper. Donec tristique, orci sed semper lacinia, quam erat rhoncus massa, non congue tellus est quis tellus. Sed mollis orci venenatis quam scelerisque accumsan. Curabitur a massa sit amet mi accumsan mollis sed et magna. Vivamus sed aliquam risus. Nulla eget dolor in elit facilisis mattis. Ut aliquet luctus lacus. Phasellus nec commodo erat. Praesent tempus id lectus ac scelerisque. Maecenas pretium cursus lectus id volutpat.",
          "sources": [
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4"
          ],
          "thumb": "images/WeAreGoingOnBullrun.jpg",
          "image-480x270": "images_480x270/WeAreGoingOnBullrun.jpg",
          "image-780x1200": "images_780x1200/Bullrun-780x1200.jpg",
          "title": "We Are Going On Bullrun",
          "studio": "Garage419"
        },
        {
          "subtitle": "Fusce id nisi turpis. Praesent viverra bibendum semper. Donec tristique, orci sed semper lacinia, quam erat rhoncus massa, non congue tellus est quis tellus. Sed mollis orci venenatis quam scelerisque accumsan. Curabitur a massa sit amet mi accumsan mollis sed et magna. Vivamus sed aliquam risus. Nulla eget dolor in elit facilisis mattis. Ut aliquet luctus lacus. Phasellus nec commodo erat. Praesent tempus id lectus ac scelerisque. Maecenas pretium cursus lectus id volutpat.",
          "sources": [
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WhatCarCanYouGetForAGrand.mp4"
          ],
          "thumb": "images/WhatCarCanYouGetForAGrand.jpg",
          "image-480x270": "images_480x270/WhatCarCanYouGetForAGrand.jpg",
          "image-780x1200": "images_780x1200/grand-780x1200.jpg",
          "title": "What care can you get for a grand?",
          "studio": "Garage419"
        },
        {
          "subtitle": "Fusce id nisi turpis. Praesent viverra bibendum semper. Donec tristique, orci sed semper lacinia, quam erat rhoncus massa, non congue tellus est quis tellus. Sed mollis orci venenatis quam scelerisque accumsan. Curabitur a massa sit amet mi accumsan mollis sed et magna. Vivamus sed aliquam risus. Nulla eget dolor in elit facilisis mattis. Ut aliquet luctus lacus. Phasellus nec commodo erat. Praesent tempus id lectus ac scelerisque. Maecenas pretium cursus lectus id volutpat.",
          "sources": [
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Google_I_O_2013_Keynote.mp4"
          ],
          "thumb": "images/WhatCarCanYouGetForAGrand.jpg",
          "image-480x270": "images_480x270/Google_I_O_2013_Keynote-480x270.jpg",
          "image-780x1200": "images_780x1200/Google_I_O_2013_Keynote-780x1200.jpg",
          "title": "Google I/O Keynote 2013",
          "studio": "Google"
        }
      ]
    }
  ]
}        
"""