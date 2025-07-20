// src/Utility/NotificationUtil.tsx

import { notifications } from "@mantine/notifications";
import { IconCheck, IconX } from "@tabler/icons-react";

// Success Notification
export const successNotification = (message: string) => {
  notifications.show({
    title: "Success",
    message: message,
    color: "teal",
    icon: <IconCheck size={20} />,
    withCloseButton: true,
    withBorder: true,
    className: "!border-green-500",
    position: "top-center", // Optional if you set default in App.tsx
  });
};

// Error Notification
export const errorNotification = (message: string) => {
  notifications.show({
    title: "Error",
    message: message,
    color: "red",
    icon: <IconX size={20} />,
    withCloseButton: true,
    withBorder: true,
    autoClose: 3000,
    className: "!border-red-500", // 🔧 Fix: red border instead of green
    position: "top-center", // Optional if you set default in App.tsx
  });
};
