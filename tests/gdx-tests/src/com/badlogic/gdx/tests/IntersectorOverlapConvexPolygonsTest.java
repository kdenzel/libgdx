package com.badlogic.gdx.tests;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.tests.utils.GdxTest;

public class IntersectorOverlapConvexPolygonsTest extends GdxTest {

    private static final String TAG = IntersectorOverlapConvexPolygonsTest.class.getSimpleName();
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;

    private float shapeWidth = 10f;
    private float shapeHeight = 10f;
    //the vertices of the 2 shapes. Feel free to play with the values. It must be a convex Polygon.
    private float[] vertsShape1 = {0f, 0f, shapeWidth, 0f, shapeWidth, shapeHeight};
    private float[] vertsShape2 = {0f, 0f, shapeWidth *2, 0f, shapeWidth *2, shapeHeight *2,0, shapeHeight};

    private Polygon shape1 = new Polygon();
    private Polygon shape2 = new Polygon();

    private Intersector.MinimumTranslationVector mtv = new Intersector.MinimumTranslationVector();

    private Vector3 mouseCoords = new Vector3();

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        camera.position.set(0, 0, 0);
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        //set inital position
        shape1.setVertices(vertsShape1);
        shape2.setVertices(vertsShape2);
        shape1.setPosition(0, 0);
        shape2.setPosition(10, 0);
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = 80;
        camera.viewportHeight = 60;
    }

    private void update(float deltaTime) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            Intersector.overlapConvexPolygons(shape1, shape2, mtv);
            float x = shape1.getX() + (mtv.normal.x * mtv.depth);
            float y = shape1.getY() + (mtv.normal.y * mtv.depth);
            shape1.setPosition(x, y);
            Gdx.app.debug(TAG, mtv.normal + " " + mtv.depth);
        }
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            mouseCoords.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(mouseCoords);
            shape1.setPosition(mouseCoords.x, mouseCoords.y);
            boolean overlaps = Intersector.overlapConvexPolygons(shape1, shape2, mtv);
            Gdx.app.debug(TAG, mtv.normal + " " + mtv.depth + " overlaps: " + overlaps + " " + mouseCoords);
        } else if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            shape1.rotate(90);
            boolean overlaps = Intersector.overlapConvexPolygons(shape1, shape2, mtv);
            Gdx.app.debug(TAG, mtv.normal + " " + mtv.depth + " overlaps: " + overlaps);
        }
    }

    @Override
    public void render() {
        update(Gdx.graphics.getDeltaTime());
        camera.update();
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.GOLD);
        shapeRenderer.polygon(shape1.getTransformedVertices());
        shapeRenderer.setColor(Color.CYAN);
        shapeRenderer.polygon(shape2.getTransformedVertices());
        //Translate the axis to position 0/0 and show the direction as red line
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.line(0, 0, (mtv.normal.x * 100)-mtv.normal.x, (mtv.normal.y * 100)-mtv.normal.y);
        //Translate the axis to position 0/0 and show the opposite direction as dark red line
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.line(0, 0, (mtv.normal.x * -100)-mtv.normal.x, (mtv.normal.y * -100)-mtv.normal.y);
        //Translate the axis to position 0/0 and show the depth as green line
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.line(0, 0, (mtv.normal.x * mtv.depth)-mtv.normal.x, (mtv.normal.y * mtv.depth)-mtv.normal.y);
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        //Show origin at 0,0 as green dot
        shapeRenderer.circle(0, 0, 0.2f);
        shapeRenderer.end();
    }
}
