import { useEffect } from 'react';
import { useThemeStore } from '../features/theme/themeStore';

export function ThemeRoot({ children }: { children: React.ReactNode }) {
  const theme = useThemeStore((state) => state.theme);
  useEffect(() => {
    document.documentElement.dataset.theme = theme;
    document.documentElement.classList.toggle('light', theme === 'light');
    document.documentElement.style.colorScheme = theme;
  }, [theme]);
  return <>{children}</>;
}
