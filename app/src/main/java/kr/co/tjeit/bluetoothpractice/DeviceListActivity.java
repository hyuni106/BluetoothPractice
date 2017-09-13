package kr.co.tjeit.bluetoothpractice;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import kr.co.tjeit.bluetoothpractice.adapter.BtNewDeviceAdapter;
import kr.co.tjeit.bluetoothpractice.adapter.BtPairedDeviceAdapter;
import kr.co.tjeit.bluetoothpractice.data.BTDevice;

public class DeviceListActivity extends BaseActivity {
    private BluetoothAdapter mBtAdapter;
    private android.widget.Button scanBtn;
    private android.widget.ListView newDeviceListView;
    private android.widget.ListView pairedDeviceListView;

    List<BTDevice> newDeviceList = new ArrayList<>();
    BtNewDeviceAdapter mBtListAdapter;

//    기존에 페어링된 기기들을 보여주기 위한 리스트/어댑터
    List<BTDevice> pairedDeviceList = new ArrayList<>();
    BtPairedDeviceAdapter pairedDeviceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        bindViews();
        setupEvents();
        setValues();
    }

    @Override
    public void setupEvents() {
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                버튼을 누르면 탐색 시작
                doDiscovery();

//                탐색이 진행중일땐 다시 탐색을 시작할 수 있도록
                scanBtn.setVisibility(View.GONE);
            }
        });
    }

//    주변의 블루투스 기기를 탐색
    void doDiscovery() {
//        새 기기 목록 리스트뷰 표시, 페어링 된 목록 리스트 숨김
        newDeviceListView.setVisibility(View.VISIBLE);
        pairedDeviceListView.setVisibility(View.GONE);

//        만약 이미 기기 탐색이 진행중이라면, 진행중이던 요청 취소
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        mBtAdapter.startDiscovery();
    }

//    화면이 메모리에서 해제될 떄(완전히 사라질 때) 실행되는 메소드
    @Override
    protected void onDestroy() {
        super.onDestroy();
//        만약 탐색 작업이 진행중이었다면 탐색 종료
        if (mBtAdapter != null) {
//            무작정 취소를 날려도 상황에 따라 알아서 정지기능 실행
            mBtAdapter.cancelDiscovery();
        }

//        브로드캐스트 리시버의 기능을 해제
        unregisterReceiver(mReceiver);
    }

    @Override
    public void setValues() {
//        블루투스 어댑터 초기화
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

//        기존에 페어링된 기기를 보여줄 리스트뷰 세팅
        pairedDeviceAdapter = new BtPairedDeviceAdapter(mContext, pairedDeviceList);;
        pairedDeviceListView.setAdapter(pairedDeviceAdapter);

//        페어링 된 적이 있는 기기들의 목록 가져옴
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
//            페어링 된 기기를 가지고 있다
//            페어링 목록 보여주고 새 기기 목록 숨겨줌
            newDeviceListView.setVisibility(View.GONE);
            pairedDeviceListView.setVisibility(View.VISIBLE);

//            페어링된 기기 목록을 리스트에 추가하고 데이터 새로고침
            for (BluetoothDevice device : pairedDevices) {
                pairedDeviceList.add(new BTDevice(device.getName(), device.getAddress()));
            }
            pairedDeviceAdapter.notifyDataSetChanged();
        }

//        탐색된 블루투스 기기를 보여줄 리스트뷰 세팅
        mBtListAdapter = new BtNewDeviceAdapter(mContext, newDeviceList);
        newDeviceListView.setAdapter(mBtListAdapter);

//        브로드 캐스트 리시버 등록
//        수신하고 싶은 방송의 종류 설정
//        1. 블루투스 기기를 찾았다 라는 방송을 받겠다
        IntentFilter foundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

//        수신하고 싶은 방송 등록
//        재료 2가지 필요
//        1. 방송이 수신되었을 때 진행할 행동 담은 Receiver
//        2. 어떤 방송을 수신할 지 설정해둔 IntentFilter
        registerReceiver(mReceiver, foundFilter);

//        방송을 수신하고자 함 > 기기 탐색이 종료되었음을 알리는 방송
        IntentFilter discoveryEndFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, discoveryEndFilter);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//           블루투스 기기를 찾았을 때 수신되는 신호를 가지고 Event 처리

//            어떤 방송이 수신되었나
            String actionName = intent.getAction();
//            1. 기기 탐색(Discovering) 결과 어떤 기기를 발견한 경우
            if (actionName.equals(BluetoothDevice.ACTION_FOUND)) {

                Toast.makeText(context, "기기를 찾았습니다.", Toast.LENGTH_SHORT).show();
//                방송 데이터 안에 들어있는 블루투스 기기 클래스 받아옴
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

//                찾아낸 기기가 이미 페어링 된 적이 있다면 무시
//                새 장비가 아니기 때문 > 페어링 가능 목록에 띄워줄 필요 없음
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
//                    아직 연결된 적이 없는 기기
                    newDeviceList.add(new BTDevice(device.getName(), device.getAddress()));
                    mBtListAdapter.notifyDataSetChanged();
                }
            }
            else if (actionName.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                if (newDeviceList.size() == 0) {
//                    새로 검색된 기기가 없다면
                    Toast.makeText(context, "검색된 기기가 없습니다.", Toast.LENGTH_SHORT).show();
//                    다시 탐색할 수 있도록 버튼 표시
                    scanBtn.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    @Override
    public void bindViews() {
        this.pairedDeviceListView = (ListView) findViewById(R.id.pairedDeviceListView);
        this.newDeviceListView = (ListView) findViewById(R.id.newDeviceListView);
        this.scanBtn = (Button) findViewById(R.id.scanBtn);
    }
}
