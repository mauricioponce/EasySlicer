package com.eme.easyslicer

import android.graphics.PointF

data class Point (val x: Float, val y: Float)


fun getCoords(center: PointF, slices: Int, radius: Float) : List<PointF> {
    val coords = mutableListOf<PointF>()
    getCoords(Point(center.x, center.y), slices, radius).map { coords.add(PointF(it.x, it.y)) }
    return coords
}

fun getCoords(center : Point, slices: Int, radius: Float): List<Point> {
    val coords = mutableListOf<Point>()
    var endX: Float
    var endY: Float

    val radAngle = Math.toRadians((360 / slices.toFloat()).toDouble())
    var tmpRadAngle = radAngle
    for (i in 0 until slices) {
        endX = (center.x + radius * Math.sin(tmpRadAngle)).toFloat()
        endY = (center.y + radius * Math.cos(tmpRadAngle)).toFloat()

        val pointF = Point(endX, endY)
        coords.add(pointF)
        tmpRadAngle += radAngle
    }
    return coords
}