import { ActionIcon, Tooltip, useMantineColorScheme } from "@mantine/core";
import { IconMoon, IconSun } from "@tabler/icons-react";

const ThemeToggle = () => {
  const { colorScheme, toggleColorScheme } = useMantineColorScheme();
  const isDark = colorScheme === "dark";

  return (
    <Tooltip label={isDark ? "Switch to light mode" : "Switch to dark mode"} position="bottom">
      <ActionIcon
        onClick={toggleColorScheme}
        variant="light"
        color={isDark ? "yellow" : "primary"}
        size="lg"
        radius="md"
        aria-label="Toggle color scheme"
      >
        {isDark ? <IconSun size={18} stroke={1.8} /> : <IconMoon size={18} stroke={1.8} />}
      </ActionIcon>
    </Tooltip>
  );
};

export default ThemeToggle;
