import type { ButtonHTMLAttributes, ReactNode } from 'react';
import { clsx } from '../../utils/cn';
interface Props extends ButtonHTMLAttributes<HTMLButtonElement> { variant?: 'primary' | 'secondary' | 'ghost'; size?: 'sm' | 'md'; children: ReactNode }
export function Button({ variant = 'secondary', size = 'md', className, children, ...props }: Props) {
  return <button className={clsx('ui-button', variant, size, className)} {...props}>{children}</button>;
}
