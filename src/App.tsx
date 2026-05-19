export default function App() {
  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center p-4 font-sans">
      <div className="max-w-md w-full bg-white rounded-2xl shadow-xl overflow-hidden border border-gray-100">
        <div className="bg-indigo-600 p-6 text-center">
          <h1 className="text-2xl font-bold text-white">Project Ready</h1>
          <p className="text-indigo-100 mt-2">Call Reason Logger</p>
        </div>
        
        <div className="p-6 space-y-6">
          <div className="flex items-center space-x-3 text-green-700 bg-green-50 px-4 py-3 border border-green-100 rounded-xl">
            <svg className="w-6 h-6 flex-shrink-0" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
            </svg>
            <p className="text-sm font-medium">Successfully generated Android Java code!</p>
          </div>

          <div className="space-y-4 text-sm text-gray-700 bg-gray-50 p-5 rounded-xl border border-gray-200">
            <h2 className="font-bold text-gray-900 mb-1">How to export:</h2>
            <ol className="list-decimal pl-5 space-y-2">
               <li>Click the Settings icon (⚙️) or ellipsis menu in the top right of the editor.</li>
               <li>Select <strong>Export</strong> and download as a ZIP file.</li>
               <li>Extract the ZIP and open it in <strong>Android Studio</strong> or <strong>AIDE</strong> on your mobile device.</li>
            </ol>
          </div>
        </div>
      </div>
    </div>
  );
}
