package com.evw.aster

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.widget.EditText
import android.widget.LinearLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.filament.utils.HDRLoader
import dev.romainguy.kotlin.math.lookAt
import io.github.sceneview.SceneView
import io.github.sceneview.environment.loadEnvironment
import io.github.sceneview.math.Direction
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode

class ProfileActivity : AppCompatActivity() {
    lateinit var editText: EditText
    lateinit var sceneView: SceneView
    lateinit var linearLayout1: LinearLayout
    lateinit var linearLayout2: LinearLayout
    lateinit var keyboard: Keyboard
    lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        recyclerView = findViewById(R.id.Recyler_view)
        sceneView = findViewById(R.id.sceneview)
        linearLayout2 = findViewById(R.id.ppp)
        editText = findViewById(R.id.primaryedit)
        keyboard = findViewById(R.id.myboard)
        editText.setRawInputType(InputType.TYPE_CLASS_TEXT)
        editText.setTextIsSelectable(true)
        val inputConnection: InputConnection = editText.onCreateInputConnection(EditorInfo())
        keyboard.setInputConnection(inputConnection)
//     val param = LinearLayout.LayoutParams(
//            LinearLayout.LayoutParams.MATCH_PARENT,
//            LinearLayout.LayoutParams.WRAP_CONTENT,
//            0f)
//        linearLayout2.setLayoutParams(param)
//        linearLayout1.setOnClickListener {
//            val param1 = LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                0.6f)
//            linearLayout2.setLayoutParams(param1)
//        }
        val modelNode = ModelNode(
            position = Position(x = 0.0f, y = 0.0f, z = 0.0f))
        sceneView.addChild(modelNode)

        sceneView.cameraNode.transform = lookAt(
            eye = modelNode.worldPosition.let {
                Position(x = it.x - 0.28f, y = it.y + 0.2f, z = it.z + 0.1f)
            },
            target = modelNode.worldPosition,
            up = Direction(y = 1.0f)
        )

        lifecycleScope.launchWhenCreated {
            sceneView.environment = HDRLoader.loadEnvironment(
                context = this@ProfileActivity,
                lifecycle = lifecycle,
                hdrFileLocation = "environments/studio_small_08_1k.hdr",
                specularFilter = true
            )?.apply {
                indirectLight?.intensity = 50_000f
            }
            modelNode.loadModel(
                context = this@ProfileActivity,
                lifecycle = lifecycle,
                glbFileLocation = "models/male.glb",
                scaleToUnits = 0.31f,
                centerOrigin = Position(x = 1.4f, y = -0.1f, z = 0.2f))
        }
    }
}









