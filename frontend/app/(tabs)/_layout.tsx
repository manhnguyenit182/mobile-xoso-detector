import { Tabs } from 'expo-router';
import React from 'react';

import { HapticTab } from '@/components/haptic-tab';
import { IconSymbol } from '@/components/ui/icon-symbol';
import { Brand } from '@/constants/theme';

export default function TabLayout() {
  return (
    <Tabs
      screenOptions={{
        tabBarActiveTintColor: Brand.gold,
        tabBarInactiveTintColor: '#5B4B7B',
        headerShown: false,
        tabBarButton: HapticTab,
        tabBarStyle: {
          backgroundColor: Brand.darkCard,
          borderTopColor: Brand.darkCard2,
          borderTopWidth: 1,
          height: 64,
          paddingBottom: 8,
        },
        tabBarLabelStyle: {
          fontSize: 11,
          fontWeight: '700',
        },
      }}
    >
      <Tabs.Screen
        name="index"
        options={{
          title: 'Quét Vé',
          tabBarIcon: ({ color }) => <IconSymbol size={26} name="camera.fill" color={color} />,
        }}
      />
      <Tabs.Screen
        name="explore"
        options={{
          title: 'Dò Tay',
          tabBarIcon: ({ color }) => <IconSymbol size={26} name="magnifyingglass" color={color} />,
        }}
      />
      <Tabs.Screen
        name="history"
        options={{
          title: 'Lịch Sử',
          tabBarIcon: ({ color }) => <IconSymbol size={26} name="clock.fill" color={color} />,
        }}
      />
    </Tabs>
  );
}

