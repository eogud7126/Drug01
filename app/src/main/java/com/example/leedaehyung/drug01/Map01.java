package com.example.leedaehyung.drug01;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import org.w3c.dom.Document;


import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class Map01 extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap gMap; // GoogleMap 객체 선언

    String[] name = new String[2467]; //xml 파일에서 병원의 이름들을 저장, 배열의 크기는 병원 수가 가장 많은 강남의 병원 수를 이용
    String[] tel = new String[2467]; // xml 파일에서 병원의 전화번호들을 저장
    String[] endtime = new String[2467]; // xml 파일에서 병원이 문 닫는 시간을 저장
    String[] starttime = new String[2467]; // xml 파일에서 병원이 문 여는 시간을 저장
    ProgressDialog asyncDialog; //progressDialog 변수
    Double[] Lat = new Double[2467]; // xml 파일에서 병원의 위도를 저장
    Double[] Lon = new Double[2467]; // xml 파일에서 병원의 경도를 저장

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map01);
        new AsyncProgressDialog().execute(1); // AsyncTask 실행
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        gMap = googleMap; // GoogleMap 객체를 전달받아 gMap에 저장

        if (gMap != null) {
            LatLng latLng1 = new LatLng(Lat[0], Lon[0]);
            CameraPosition position = new CameraPosition.Builder()
                    .target(latLng1).zoom(16f).build(); // 지도를 띄울때 특정 병원을 중심으로 지도를 띄움
            gMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));

            for (int i = 0; i < 2467; i++) {
                if (Lat[i] == null)
                    break;
                LatLng latLng = new LatLng(Lat[i], Lon[i]);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker));//이미지
                markerOptions.position(latLng);//마커 위치
                markerOptions.title(name[i]);
                if (starttime[i].length() == 3) //xml에 파일에 시간이 930이런식으로 된 것이 잇어서 앞에 0을 붙여줌으로써 시간을 맞춰줌
                    markerOptions.snippet("전화번호: " + tel[i] + " 영업시간: 0" + starttime[i] + " ~ " + endtime[i]);
                else
                    markerOptions.snippet("전화번호: " + tel[i] + " 영업시간: " + starttime[i] + " ~ " + endtime[i]);
                gMap.addMarker(markerOptions);//지도 위에 마커 표시(추가)
                gMap.setOnInfoWindowClickListener(infoWindowClickListener); //  마커 설명에 onclicklistener 설정
            }
        }
    }

    GoogleMap.OnInfoWindowClickListener infoWindowClickListener = new GoogleMap.OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick(Marker marker) {
            String[] tmp=marker.getSnippet().split(" "); // 마커 설명에서 snippet을 가져와서 공백 단위로 나눠서 tmp에 저장
            String tel = "tel:"+tmp[1]; // 전화번호 부분에 tel:을 붙여서 string 생성
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(tel)); // 인텐트 생성
            startActivity(intent);
        }
    };
    private class AsyncProgressDialog extends AsyncTask<Integer, String, Integer> {

        @Override
        protected void onPreExecute() { // 백그라운드 작업 시작전에 작업 진행
            super.onPreExecute();
            asyncDialog = new ProgressDialog(Map01.this); //다이얼 로그 생성
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // SPINNER 스타일로 지정
            asyncDialog.setTitle("병원을 불러오는 중입니다..."); //ProgressingDialog 제목
            asyncDialog.setMessage("Loading..."); // ProgressDialog 메시지

            asyncDialog.show();
        }

        @Override
        protected Integer doInBackground(Integer... integers) {

            try {
                Intent intent = getIntent();
                String Gu = intent.getStringExtra("Gu");
                InputStream inputStream = getAssets().open(Gu + ".xml");  //assets 폴더의 test.xml 파일 읽기

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); // 빌드 팩토리 객체를 생성
                DocumentBuilder builder = factory.newDocumentBuilder(); // builder 객체 획득

                Document doc = builder.parse(inputStream, null);
                for (int i = 0; i < 2467; i++) {
                    org.w3c.dom.Element nameElement = (org.w3c.dom.Element) doc.getElementsByTagName("dutyName").item(i);
                    if (nameElement.getTextContent() == null)
                        break; //받아 올 값이 없으면 null
                    name[i] = nameElement.getTextContent();

                    org.w3c.dom.Element telElement = (org.w3c.dom.Element) doc.getElementsByTagName("dutyTel1").item(i);
                    tel[i] = telElement.getTextContent();

                    org.w3c.dom.Element ctimeElement = (org.w3c.dom.Element) doc.getElementsByTagName("dutyTime1c").item(i);
                    endtime[i] = ctimeElement.getTextContent();

                    org.w3c.dom.Element stimeElement = (org.w3c.dom.Element) doc.getElementsByTagName("dutyTime1s").item(i);
                    starttime[i] = stimeElement.getTextContent();

                    org.w3c.dom.Element LatElement = (org.w3c.dom.Element) doc.getElementsByTagName("wgs84Lat").item(i);
                    Lat[i] = Double.parseDouble(LatElement.getTextContent());

                    org.w3c.dom.Element LonElement = (org.w3c.dom.Element) doc.getElementsByTagName("wgs84Lon").item(i);
                    Lon[i] = Double.parseDouble(LonElement.getTextContent());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) { // 백그라운드 작업이 끝난 후 작업
            super.onPostExecute(result);
            ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment)).getMapAsync(Map01.this); // 프래그먼트에서 getMapAsync 함수를 이용해 구현한 인터페이스 콜백을 등록
            asyncDialog.dismiss(); //다이얼로그 닫기
        }
    }
}

