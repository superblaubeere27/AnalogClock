/*
 * Copyright (c) 2018 superblaubeere27
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.superblaubeere27.analogclock;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.PixelFormat;

import java.time.LocalDateTime;

import static org.lwjgl.opengl.GL11.*;

public class Main {

    public static void main(String args[]) {
        System.out.println("Starting LLVM " + Sys.getVersion());

        start();
    }

    private static void start() {
        try {
//            Display.setDisplayMode(new DisplayMode(1280, 720));
            Display.setDisplayMode(Display.getDesktopDisplayMode());
            Display.setFullscreen(true);
            Display.setVSyncEnabled(true);
            Display.setTitle("Analog Clock");
            Display.create(new PixelFormat(0, 8, 0, 16)); // Samples = MSAA x8
        } catch (LWJGLException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        initGL();

        while (!Display.isCloseRequested()) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            render();

            Display.update();
        }
    }

    private static void render() {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        LocalDateTime now = LocalDateTime.now();

        glDisable(GL_TEXTURE_2D);

        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        double clockRadius = Display.getHeight() * 0.49;

        glPushMatrix();

        glTranslated(Display.getWidth() / 2.0, Display.getHeight() / 2.0, 0);

        glLineWidth(1.0f);
//
        double seconds = now.getSecond() + System.currentTimeMillis() % 1000 / 1000.0;
        double minutes = now.getMinute() + seconds / 60.0;
        double hours = now.getHour() + minutes / 60.0;

        // Seconds
        drawHand(1.0f, seconds / 60.0 * 100.0, clockRadius);
        // Minutes
        drawHand(4.0f, minutes / 60.0 * 100.0, clockRadius * 0.75);
        // Hours
        drawHand(6.0f, (hours % 12.0) / 12.0 * 100.0, clockRadius * 0.5);


        glLineWidth(1.0f);
        drawCircle(clockRadius, false);
        drawCircle(8, true);

        glPopMatrix();
    }

    private static void drawHand(float width, double percent, double length) {
        double conv = convertPercentToRadians(percent);

        glLineWidth(width);

        glBegin(GL_LINES);

        glVertex2d(0, 0);
        glVertex2d(-length * Math.sin(conv), length * Math.cos(conv));

        glEnd();

    }

    private static double convertPercentToRadians(double percent) {
        return percent / 100.0 * Math.PI * 2 + Math.PI;
    }

    private static void drawCircle(double radius, boolean fill) {

        double twicePi = Math.PI * 2;

        int triageAmount = (int) Math.max(15, radius * twicePi / 15);

        glBegin(fill ? GL_TRIANGLE_FAN : GL_LINE_LOOP);

        for (int i = 0; i <= triageAmount; i++) {
            glVertex2d(
                    radius * Math.cos(i * twicePi / triageAmount),
                    radius * Math.sin(i * twicePi / triageAmount)
            );
        }

        glEnd();
    }

    private static void initGL() {
        glEnable(GL_TEXTURE_2D);

        glShadeModel(GL_SMOOTH);

        glDisable(GL_DEPTH_TEST);
        glDisable(GL_LIGHTING);

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glClearDepth(1.0f);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glViewport(0, 0, Display.getWidth(), Display.getHeight());
        glMatrixMode(GL_MODELVIEW);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
        glMatrixMode(GL_MODELVIEW);

    }

}
