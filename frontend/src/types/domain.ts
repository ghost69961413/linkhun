export type ConnectionStatus = 'PENDING' | 'ACCEPTED' | 'REJECTED';
export interface ConnectionUser { userId: number; username: string; fullName: string; headline?: string | null; profileImage?: string | null }
export interface ConnectionRequest { requestId: number; status: ConnectionStatus; createdAt: string; sender: ConnectionUser; recipient: ConnectionUser }
export interface NavigationItem { label: string; path: string; icon: string }
export type ThemeMode = 'dark' | 'light';
