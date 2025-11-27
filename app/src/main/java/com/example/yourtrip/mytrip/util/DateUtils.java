package com.example.yourtrip.mytrip.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List; // List import 추가
import java.util.ArrayList; // ArrayList import 추가
import java.util.Locale;

/**
 * 앱 전체에서 날짜 관련 계산을 도와주는 공용 유틸리티 클래스입니다.
 * 중복 코드를 제거하여 유지보수성을 높였습니다.
 */
public class DateUtils {

    /**
     * "yyyy-MM-dd" 형식의 날짜 문자열을 "yyyy년 M월 d일" 형식으로 변환합니다.
     */
    public static String formatKoreanDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) return "";
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
            Date date = inputFormat.parse(dateString);
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy년 M월 d일", Locale.KOREA);
            return outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return dateString;
        }
    }

    /**
     * 시작일과 종료일로 "N박 N+1일" 또는 "당일치기" 텍스트를 생성합니다.
     * 이제 내부적으로 getDiffDays 헬퍼 메서드를 호출하여 중복을 제거합니다.
     */
    public static String getNightDayText(String start, String end) {
        // ★ 1. 날짜 차이를 계산하는 공통 로직을 호출합니다.
        long diffDays = getDiffDays(start, end);

        if (diffDays == -1) { // getDiffDays에서 오류가 발생한 경우
            return "";
        }
        if (diffDays == 0) {
            return "당일치기";
        }

        long nights = diffDays;
        long days = diffDays + 1;
        return nights + "박 " + days + "일";
    }

    /**
     * 시작일과 종료일로 총 여행 일수를 계산합니다. (예: 2박 3일 -> 3)
     * 이제 내부적으로 getDiffDays 헬퍼 메서드를 호출하여 중복을 제거합니다.
     */
    public static int getTotalTripDays(String start, String end) {
        // ★ 2. 날짜 차이를 계산하는 공통 로직을 호출합니다.
        long diffDays = getDiffDays(start, end);

        if (diffDays == -1) { // getDiffDays에서 오류가 발생한 경우
            return 0;
        }

        // 총 여행 일수는 (날짜 차이 + 1) 입니다.
        return (int) diffDays + 1;
    }

    /**
     * (임시) 시작일과 종료일로 "1일차", "2일차" 등의 리스트를 생성합니다.
     */
    public static List<String> getTripDaysList(String start, String end) {
        List<String> dayList = new ArrayList<>();
        int totalDays = getTotalTripDays(start, end);

        if (totalDays > 0) {
            for (int i = 1; i <= totalDays; i++) {
                dayList.add(i + "일차");
            }
        }
        // "일정 추가하기" 버튼은 이제 여기서 관리하지 않습니다.
        return dayList;
    }


    /**
     * ★★★ 중복 코드를 추출한 비공개(private) 헬퍼 메서드 ★★★
     * 시작일과 종료일의 날짜 차이(일)를 계산하여 반환합니다.
     * @return 날짜 차이(일). 오류 발생 시 -1을 반환.
     */
    private static long getDiffDays(String start, String end) {
        if (start == null || end == null || start.isEmpty() || end.isEmpty()) {
            return -1;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
            Date startDate = sdf.parse(start);
            Date endDate = sdf.parse(end);
            long diffMillis = endDate.getTime() - startDate.getTime();
            return diffMillis / (24 * 60 * 60 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
            return -1; // 오류 발생 시 -1 반환
        }
    }
}


//package com.example.yourtrip.mytrip.util;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Locale;
//
///**
// * 앱 전체에서 날짜 관련 계산을 도와주는 공용 유틸리티 클래스
// * 어댑터나 액티비티에 종속되지 않아 어디서든 재사용 가능
// */
//public class DateUtils {
//
//    /**
//     * "yyyy-MM-dd" 형식의 날짜 문자열을 "yyyy년 M월 d일" 형식으로 변환
//     * @param dateString 변환할 날짜 문자열
//     * @return 변환된 한국어 날짜 형식의 문자열
//     */
//    public static String formatKoreanDate(String dateString) {
//        if (dateString == null || dateString.isEmpty()) return "";
//        try {
//            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
//            Date date = inputFormat.parse(dateString);
//            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy년 M월 d일", Locale.KOREA);
//            return outputFormat.format(date);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return dateString; // 파싱 실패 시 원본 반환
//        }
//    }
//
//    /**
//     * 시작일과 종료일로 "N박 N+1일" 또는 "당일치기" 텍스트를 생성
//     * @param start 시작일 문자열 (yyyy-MM-dd)
//     * @param end 종료일 문자열 (yyyy-MM-dd)
//     * @return 계산된 기간 텍스트
//     */
//    public static String getNightDayText(String start, String end) {
//        if (start == null || end == null || start.isEmpty() || end.isEmpty()) return "";
//        try {
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
//            Date startDate = sdf.parse(start);
//            Date endDate = sdf.parse(end);
//            long diffMillis = endDate.getTime() - startDate.getTime();
//            long diffDays = diffMillis / (24 * 60 * 60 * 1000);
//
//            if (diffDays == 0) {
//                return "당일치기";
//            }
//
//            long nights = diffDays;
//            long days = diffDays + 1;
//            return nights + "박 " + days + "일";
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "";
//        }
//    }
//
//    public static int getTotalTripDays(String start, String end) {
//        if (start == null || end == null || start.isEmpty() || end.isEmpty()) {
//            return 0;
//        }
//        try {
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
//            Date startDate = sdf.parse(start);
//            Date endDate = sdf.parse(end);
//            long diffMillis = endDate.getTime() - startDate.getTime();
//            long diffDays = diffMillis / (24 * 60 * 60 * 1000);
//
//            // 총 여행 일수는 (날짜 차이 + 1) 입니다.
//            return (int) diffDays + 1;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return 0; // 오류 발생 시 0일로 처리
//        }
//    }
//}
