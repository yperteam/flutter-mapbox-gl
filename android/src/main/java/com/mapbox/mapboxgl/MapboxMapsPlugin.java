// Copyright 2018 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.mapbox.mapboxgl;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.concurrent.atomic.AtomicInteger;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterPluginBinding;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * Plugin for controlling a set of MapboxMap views to be shown as overlays on top of the Flutter
 * view. The overlay should be hidden during transformations or while Flutter is rendering on top of
 * the map. A Texture drawn using MapboxMap bitmap snapshots can then be shown instead of the
 * overlay.
 */
public class MapboxMapsPlugin implements Application.ActivityLifecycleCallbacks, FlutterPlugin, ActivityAware {
  static final int CREATED = 1;
  static final int STARTED = 2;
  static final int RESUMED = 3;
  static final int PAUSED = 4;
  static final int STOPPED = 5;
  static final int DESTROYED = 6;
  private final AtomicInteger state = new AtomicInteger(0);
  private int registrarActivityHashCode;
  private Activity mActivity;

  /*public static void registerWith(Registrar registrar) {
    if (registrar.activity() == null) {
      // When a background flutter view tries to register the plugin, the registrar has no activity.
      // We stop the registration process as this plugin is foreground only.
      return;
    }
    mActivity = registrar.activity();
    final MapboxMapsPlugin plugin = new MapboxMapsPlugin(registrar);
    registrar.activity().getApplication().registerActivityLifecycleCallbacks(plugin);
    registrar
      .platformViewRegistry()
      .registerViewFactory(
        "plugins.flutter.io/mapbox_gl", new MapboxMapFactory(plugin.state, registrar));

    MethodChannel methodChannel =
            new MethodChannel(registrar.messenger(), "plugins.flutter.io/mapbox_gl");
    methodChannel.setMethodCallHandler(new GlobalMethodHandler(registrar));
  }*/

  @Override
  public void onAttachedToActivity(ActivityPluginBinding binding) {
    mActivity = binding.getActivity();
    this.registrarActivityHashCode = mActivity.hashCode();
    mActivity.getApplication().registerActivityLifecycleCallbacks(this);
  }

  @Override
  public void onReattachedToActivityForConfigChanges(ActivityPluginBinding binding) {
    mActivity = binding.getActivity();
    this.registrarActivityHashCode = mActivity.hashCode();
    mActivity.getApplication().registerActivityLifecycleCallbacks(this);
  }

  @Override
 public void onDetachedFromActivity() {
   this.registrarActivityHashCode = 0;
   if (mActivity != null)
      mActivity.getApplication().unregisterActivityLifecycleCallbacks(this);
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    this.registrarActivityHashCode = 0;
    if (mActivity != null)
      mActivity.getApplication().unregisterActivityLifecycleCallbacks(this);
  }

  @Override
  public void onAttachedToEngine(FlutterPluginBinding binding) {
    binding
      .getPlatformViewRegistry()
      .registerViewFactory(
        "plugins.flutter.io/mapbox_gl", new MapboxMapFactory(state, binding));

    MethodChannel methodChannel =
            new MethodChannel(binding.getBinaryMessenger(), "plugins.flutter.io/mapbox_gl");
    methodChannel.setMethodCallHandler(new GlobalMethodHandler(binding));
  }

  @Override
  public void onDetachedFromEngine(FlutterPluginBinding binding) {}

  @Override
  public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    if (activity.hashCode() != registrarActivityHashCode) {
      return;
    }
    state.set(CREATED);
  }

  @Override
  public void onActivityStarted(Activity activity) {
    if (activity.hashCode() != registrarActivityHashCode) {
      return;
    }
    state.set(STARTED);
  }

  @Override
  public void onActivityResumed(Activity activity) {
    if (activity.hashCode() != registrarActivityHashCode) {
      return;
    }
    state.set(RESUMED);
  }

  @Override
  public void onActivityPaused(Activity activity) {
    if (activity.hashCode() != registrarActivityHashCode) {
      return;
    }
    state.set(PAUSED);
  }

  @Override
  public void onActivityStopped(Activity activity) {
    if (activity.hashCode() != registrarActivityHashCode) {
      return;
    }
    state.set(STOPPED);
  }

  @Override
  public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
  }

  @Override
  public void onActivityDestroyed(Activity activity) {
    if (activity.hashCode() != registrarActivityHashCode) {
      return;
    }
    state.set(DESTROYED);
  }

  private MapboxMapsPlugin(Registrar registrar) {
    this.registrarActivityHashCode = registrar.activity().hashCode();
  }
}
