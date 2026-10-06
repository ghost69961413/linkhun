import { Component, type ErrorInfo, type ReactNode } from 'react';
import { AlertTriangle, RefreshCw } from 'lucide-react';

interface Props { children: ReactNode }
interface State { hasError: boolean }
export class ErrorBoundary extends Component<Props, State> {
  state: State = { hasError: false };
  static getDerivedStateFromError(): State { return { hasError: true }; }
  componentDidCatch(error: Error, info: ErrorInfo) { console.error('LinkHub render error', error, info); }
  render() {
    if (this.state.hasError) return <main className="fatal-error"><div className="fatal-card"><AlertTriangle /><h1>That didn’t load right.</h1><p>Something unexpected happened while rendering LinkHub.</p><button className="ui-button primary" onClick={() => window.location.reload()}><RefreshCw size={15}/> Reload app</button></div></main>;
    return this.props.children;
  }
}
