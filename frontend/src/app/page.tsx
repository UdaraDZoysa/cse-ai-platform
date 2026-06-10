import Link from "next/link";

export default function Home() {
  return (
    <>
      <h1 className="text-4xl font-bold text-center mt-10">
        Welcome to CSE Investment Insights
      </h1>
      <button className="mt-6 px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 transition-colors">
        <Link href="/investment-insights">
          View Investment Insights
        </Link>
      </button>     
    </>
  );
}
