package com.example.iocsrc;

import java.lang.reflect.Field;

import android.app.Activity;
import android.view.View;

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

	}

	private static void injectField(ViewFinder viewFinder, Object object) {
		Class<?> clazz = object.getClass();
		// 1����ȡ���е�����
		Field[] fields = clazz.getDeclaredFields();
		// 2���������ԣ���ȡViewByIdע��
		for (Field field : fields) {
			ViewById annotation = field.getAnnotation(ViewById.class);
			if (annotation != null) {
				int viewId = annotation.value();
				View view = viewFinder.findViewById(viewId);
				if (view != null) {
					// 3����viewע�뵽��ǰfiled��
					field.setAccessible(true);
					try {
						field.set(object, view);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}
}
