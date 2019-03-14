package com.example.iocsrc;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class ViewUtils {
	public static void inject(Activity activity) {
		inject(new ViewFinder(activity), activity);
	}

	public static void inject(View view) {
		inject(new ViewFinder(view), view);
	}

	private static void inject(ViewFinder viewFinder, Object object) {
		injectField(viewFinder, object);
		injectEvent(viewFinder, object);
	}

	private static void injectEvent(ViewFinder viewFinder, Object object) {
		Class<?> clazz = object.getClass();
		// 1、获取注解类的所有方法
		Method[] methods = clazz.getDeclaredMethods();
		// 2、遍历方法，查看哪个方法有OnClick的注解
		for (Method method : methods) {
			OnClick annotation = method.getAnnotation(OnClick.class);
			if (annotation != null) {

				// 4、加上是否有@CheckNet注解
				boolean checkNet = (method.getAnnotation(CheckNet.class) != null);
				// 3、获取到OnClick注解之后，获取注解的值，根据值获取对象
				int[] viewIds = annotation.values();
				if (viewIds.length > 0) {
					for (int viewId : viewIds) {
						View view = viewFinder.findViewById(viewId);
						if (view != null) {
							view.setOnClickListener(new DeclaredOnClickListener(
									method, object, checkNet));
						}
					}
				}
			}
		}
	}

	private static class DeclaredOnClickListener implements OnClickListener {
		private Method mMethod;
		private Object mHandler;
		private boolean mCheckNet;

		public DeclaredOnClickListener(Method method, Object handler,
				boolean checkNet) {
			mMethod = method;
			mHandler = handler;
			this.mCheckNet = checkNet;
		}

		@Override
		public void onClick(View v) {
			// 首先判断需要网络的判断
			if (mCheckNet) {
				// 需要判断网络是否可用
				if (!isNetworkAvaible(v.getContext())) {
					Toast.makeText(v.getContext(), "网络不给力", Toast.LENGTH_SHORT)
							.show();
					return;
				}
			}
			mMethod.setAccessible(true);
			try {
				mMethod.invoke(mHandler, v);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();

			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	private static void injectField(ViewFinder viewFinder, Object object) {
		Class<?> clazz = object.getClass();
		// 1、获取所有的属性
		Field[] fields = clazz.getDeclaredFields();
		// 2、遍历属性，获取ViewById注解
		for (Field field : fields) {
			ViewById annotation = field.getAnnotation(ViewById.class);
			if (annotation != null) {
				int viewId = annotation.value();
				View view = viewFinder.findViewById(viewId);
				if (view != null) {
					// 3、将view注入到当前filed中
					field.setAccessible(true);
					try {
						field.set(object, view);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					}
				} else {
					throw new RuntimeException("Invalid @ViewIById Inject for "
							+ clazz.getSimpleName() + "." + field.getName());
				}
			}
		}

	}

	private static boolean isNetworkAvaible(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getApplicationContext().getSystemService(
						Context.CONNECTIVITY_SERVICE);

		if (manager == null) {
			return false;
		}

		NetworkInfo networkinfo = manager.getActiveNetworkInfo();

		if (networkinfo == null || !networkinfo.isAvailable()) {
			return false;
		}

		return true;

	}
}
