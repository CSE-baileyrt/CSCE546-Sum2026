package com.example.airhockey

import android.content.Context
import android.util.AttributeSet
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View
import kotlin.math.sqrt
import kotlin.random.Random

class AirHockeyView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var screenWidth = 0f
    private var screenHeight = 0f
    private var isSizeInitialized = false

    // Puck Properties
    private var puckX = 0f
    private var puckY = 0f
    private var puckRadius = 40f
    private var puckSpeedX = 0f
    private var puckSpeedY = 0f
    private val maxSpeed = 45f

    // Scoring Configurations
    private var topScore = 0
    private var bottomScore = 0
    private val winningScore = 5
    private var gameOver = false
    private var goalWidth = 200f

    // Touch Point Trackers (Keyed by unique multi-touch Pointer ID)
    private val topFingers = HashMap<Int, PointF>()
    private val bottomFingers = HashMap<Int, PointF>()

    // Graphic Elements Configuration
    private val blackPaint = Paint().apply { color = Color.BLACK; strokeWidth = 8f; style = Paint.Style.FILL; textSize = 70f }
    private val topFingerPaint = Paint().apply { color = Color.RED; style = Paint.Style.FILL }
    private val bottomFingerPaint = Paint().apply { color = Color.BLUE; style = Paint.Style.FILL }
    private val linePaint = Paint().apply { color = Color.BLACK; strokeWidth = 10f }

    private class PointF(var x: Float, var y: Float)

    init {
        val density = context.resources.displayMetrics.density
        goalWidth = 200f * density // Dynamic calculation for 200dpi
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        screenWidth = w.toFloat()
        screenHeight = h.toFloat()

        if (w > 0 && h > 0) {
            resetPuck()
            isSizeInitialized = true
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 1. Clear screen with White
        canvas.drawColor(Color.WHITE)

        if (!isSizeInitialized) return

        // 2. Update Game State
        if (!gameOver) {
            updatePhysics()
        }

        // 3. Draw Field and Objects
        val midY = screenHeight / 2f
        val midX = screenWidth / 2f
        val goalLeft = midX - (goalWidth / 2f)
        val goalRight = midX + (goalWidth / 2f)

        // Court Division line
        canvas.drawLine(0f, midY, screenWidth, midY, linePaint)

        // Goals Configuration
        canvas.drawRect(goalLeft, 0f, goalRight, 20f, blackPaint)
        canvas.drawRect(goalLeft, screenHeight - 20f, goalRight, screenHeight, blackPaint)

        // Render Local Scores (Beside the goals)
        canvas.drawText(topScore.toString(), goalLeft - 80f, 80f, blackPaint)
        canvas.drawText(bottomScore.toString(), goalLeft - 80f, screenHeight - 40f, blackPaint)

        // Render Game Puck
        canvas.drawCircle(puckX, puckY, puckRadius, blackPaint)

        // Render Active Touch Indicators
        for (finger in topFingers.values) {
            canvas.drawCircle(finger.x, finger.y, 50f, topFingerPaint)
        }
        for (finger in bottomFingers.values) {
            canvas.drawCircle(finger.x, finger.y, 50f, bottomFingerPaint)
        }

        // Victory Layer
        if (gameOver) {
            val winText = if (topScore >= winningScore) "Top Player Wins!" else "Bottom Player Wins!"
            blackPaint.textSize = 90f
            blackPaint.textAlign = Paint.Align.CENTER
            canvas.drawText(winText, midX, midY - 60f, blackPaint)
            blackPaint.textSize = 50f
            canvas.drawText("Tap anywhere to restart", midX, midY + 60f, blackPaint)
            blackPaint.textAlign = Paint.Align.LEFT
        }

        // 4. Force a highly optimized layout refresh loop matching the screen refresh rate
        postInvalidateOnAnimation()
    }

    private fun updatePhysics() {
        puckX += puckSpeedX
        puckY += puckSpeedY

        // Induce continuous slight random drift if velocity drops completely
        if (puckSpeedX == 0f && puckSpeedY == 0f) {
            puckSpeedX = Random.nextFloat() * 4f - 2f
            puckSpeedY = Random.nextFloat() * 4f - 2f
        }

        val currentSpeed = sqrt(puckSpeedX * puckSpeedX + puckSpeedY * puckSpeedY)
        if (currentSpeed > maxSpeed) {
            puckSpeedX = (puckSpeedX / currentSpeed) * maxSpeed
            puckSpeedY = (puckSpeedY / currentSpeed) * maxSpeed
        }

        // Left & Right boundary wall bounces
        if (puckX - puckRadius < 0) {
            puckX = puckRadius
            puckSpeedX = -puckSpeedX
        } else if (puckX + puckRadius > screenWidth) {
            puckX = screenWidth - puckRadius
            puckSpeedX = -puckSpeedX
        }

        val midX = screenWidth / 2f
        val goalLeft = midX - (goalWidth / 2f)
        val goalRight = midX + (goalWidth / 2f)

        // Top Goal / Wall detection
        if (puckY - puckRadius < 0) {
            if (puckX in goalLeft..goalRight) {
                bottomScore++
                checkWinCondition()
                resetPuck()
            } else {
                puckY = puckRadius
                puckSpeedY = -puckSpeedY
            }
        }

        // Bottom Goal / Wall detection
        if (puckY + puckRadius > screenHeight) {
            if (puckX in goalLeft..goalRight) {
                topScore++
                checkWinCondition()
                resetPuck()
            } else {
                puckY = screenHeight - puckRadius
                puckSpeedY = -puckSpeedY
            }
        }

        val fingerRadius = 50f
        val collisionDistance = puckRadius + fingerRadius

        for (finger in topFingers.values) {
            handleFingerCollision(finger, collisionDistance)
        }
        for (finger in bottomFingers.values) {
            handleFingerCollision(finger, collisionDistance)
        }
    }

    private fun handleFingerCollision(finger: PointF, collisionDistance: Float) {
        val dx = puckX - finger.x
        val dy = puckY - finger.y
        val distance = sqrt(dx * dx + dy * dy)

        if (distance < collisionDistance) {
            val overlap = collisionDistance - distance
            puckX += (dx / (if (distance == 0f) 1f else distance)) * overlap
            puckY += (dy / (if (distance == 0f) 1f else distance)) * overlap

            puckSpeedX = (dx / (if (distance == 0f) 1f else distance)) * (maxSpeed * 0.75f)
            puckSpeedY = (dy / (if (distance == 0f) 1f else distance)) * (maxSpeed * 0.75f)
        }
    }

    private fun resetPuck() {
        puckX = screenWidth / 2f
        puckY = screenHeight / 2f
        puckSpeedX = if (Random.nextBoolean()) 8f else -8f
        puckSpeedY = if (Random.nextBoolean()) 8f else -8f
    }

    private fun checkWinCondition() {
        if (topScore >= winningScore || bottomScore >= winningScore) {
            gameOver = true
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val pointerCount = event.pointerCount
        val actionIndex = event.actionIndex
        val pointerId = event.getPointerId(actionIndex)
        val actionMasked = event.actionMasked
        val midY = screenHeight / 2f

        // Simple overlay click handling to reset game
        if (gameOver) {
            if (actionMasked == MotionEvent.ACTION_DOWN) {
                topScore = 0
                bottomScore = 0
                gameOver = false
                resetPuck()
            }
            return true
        }

        when (actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                val x = event.getX(actionIndex)
                val y = event.getY(actionIndex)

                if (y < midY) {
                    if (topFingers.size < 3) {
                        topFingers[pointerId] = PointF(x, y)
                    }
                } else {
                    if (bottomFingers.size < 3) {
                        bottomFingers[pointerId] = PointF(x, y)
                    }
                }
            }

            MotionEvent.ACTION_MOVE -> {
                for (i in 0 until pointerCount) {
                    val pId = event.getPointerId(i)
                    val x = event.getX(i)
                    val y = event.getY(i)

                    if (topFingers.containsKey(pId)) {
                        topFingers[pId]?.x = x
                        topFingers[pId]?.y = if (y < midY) y else midY - 5f
                    }
                    if (bottomFingers.containsKey(pId)) {
                        bottomFingers[pId]?.x = x
                        bottomFingers[pId]?.y = if (y > midY) y else midY + 5f
                    }
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_CANCEL -> {
                topFingers.remove(pointerId)
                bottomFingers.remove(pointerId)
            }
        }
        return true
    }
}