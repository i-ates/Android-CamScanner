package com.example.myapplication;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class DetectPaper {
    public static class Quadrilateral {
        public ArrayList<MatOfPoint> contour;
        public Point[] points;

        public Quadrilateral(ArrayList<MatOfPoint> contour, Point[] points) {
            this.contour = contour;
            this.points = points;
        }

    }
    public static Quadrilateral findDocument( Mat inputRgba ) {
        ArrayList<MatOfPoint> contours = findContours(inputRgba);
        Quadrilateral quad = getQuadrilateral(contours);
        return quad;
    }

    private static ArrayList<MatOfPoint> findContours(Mat src) {

        Mat grayImage = new Mat();
        Mat cannedImage = new Mat();

        Imgproc.cvtColor(src, grayImage, Imgproc.COLOR_RGBA2GRAY);
        Imgproc.GaussianBlur(grayImage, grayImage, new Size(3, 3), 2,2);
        Imgproc.Canny(grayImage, cannedImage, 80, 100,3,true);
        Imgproc.dilate(cannedImage,cannedImage,new Mat(),new Point(-1,-1),3,1,new Scalar(1));

        ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();

        Imgproc.findContours(cannedImage, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

        hierarchy.release();

        Collections.sort(contours, new Comparator<MatOfPoint>() {

            @Override
            public int compare(MatOfPoint lhs, MatOfPoint rhs) {
                return Double.compare(Imgproc.contourArea(rhs), Imgproc.contourArea(lhs));
            }
        });

        grayImage.release();
        cannedImage.release();

        return contours;
    }

    private static Quadrilateral getQuadrilateral(ArrayList<MatOfPoint> contours) {

        for ( MatOfPoint c: contours ) {
            MatOfPoint2f c2f = new MatOfPoint2f(c.toArray());
            double peri = Imgproc.arcLength(c2f, true);
            MatOfPoint2f approx = new MatOfPoint2f();
            Imgproc.approxPolyDP(c2f, approx, 0.02 * peri, true);

            Point[] points = approx.toArray();

            // select biggest 4 angles polygon
            if (points.length == 4) {
                Point[] foundPoints = sortPoints(points);

                return new Quadrilateral(contours, foundPoints);
            }
        }

        return null;
    }

    private static Point[] sortPoints(Point[] src) {

        ArrayList<Point> srcPoints = new ArrayList<>(Arrays.asList(src));

        Point[] result = { null , null , null , null };

        Comparator<Point> sumComparator = new Comparator<Point>() {
            @Override
            public int compare(Point lhs, Point rhs) {
                return Double.compare(lhs.y + lhs.x, rhs.y + rhs.x);
            }
        };

        Comparator<Point> diffComparator = new Comparator<Point>() {

            @Override
            public int compare(Point lhs, Point rhs) {
                return Double.compare(lhs.y - lhs.x, rhs.y - rhs.x);
            }
        };

        // top-left corner = minimal sum
        result[0] = Collections.min(srcPoints, sumComparator);

        // bottom-right corner = maximal sum
        result[2] = Collections.max(srcPoints, sumComparator);

        // top-right corner = minimal diference
        result[1] = Collections.min(srcPoints, diffComparator);

        // bottom-left corner = maximal diference
        result[3] = Collections.max(srcPoints, diffComparator);

        return result;
    }


}
