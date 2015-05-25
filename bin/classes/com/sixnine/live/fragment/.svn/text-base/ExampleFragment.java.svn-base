package com.sixnine.live.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sixnine.live.R;

public class ExampleFragment extends Fragment{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("ExampleFragment--onCreate");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		System.out.println("ExampleFragment--oncreateView");
		return inflater.inflate(R.layout.example_fragment_layout, container,false);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		System.out.println("ExampleFragment--onPause");
	}
	
	@Override
	public void onResume() {
		super.onResume();
		System.out.println("ExampleFragment--onResume");
	}
	
	@Override
	public void onStop() {
		super.onStop();
		System.out.println("ExampleFragment--onStop");
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		System.out.println("ExampleFragment--onAttach");
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		System.out.println("ExampleFragment--onDetach");
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		System.out.println("ExampleFragment--onActivityCreated");
	}
}
