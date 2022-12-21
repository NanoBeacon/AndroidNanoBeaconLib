package com.oncelabs.nanobeaconlib.enums

enum class ChannelMode(val channels: String, val raw: Int) {
    CTRL0(channels = "37, 38, 39", raw = 0),
    CTRL1(channels = "38, 39", raw = 1),
    CTRL2(channels = "37, 39", raw = 2),
    CTRL3(channels = "39", raw = 3),
    CTRL4(channels = "37, 38", raw = 4),
    CTRL5(channels = "38", raw = 5),
    CTRL6(channels = "37", raw = 6),
}