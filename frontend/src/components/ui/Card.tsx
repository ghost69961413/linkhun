import type { HTMLAttributes } from 'react';
import { clsx } from '../../utils/cn';
export function Card({ className, ...props }: HTMLAttributes<HTMLElement>) { return <section className={clsx('surface-card', className)} {...props}/>; }
