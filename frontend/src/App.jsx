import { useEffect } from "react";
import Lenis from "lenis";
import { CustomCursor } from "./components/CustomCursor";
import { Navbar } from "./components/Navbar";
import { HeroSection } from "./components/HeroSection";
import { FeaturesSection } from "./components/FeaturesSection";
import { CardsSection } from "./components/CardsSection";
import { Footer } from "./components/Footer";

function App() {
  // Initialize Smooth Scrolling (Lenis)
  useEffect(() => {
    const lenis = new Lenis({
      duration: 1.2,
      easing: (t) => Math.min(1, 1.001 - Math.pow(2, -10 * t)), // easeOutExpo
      direction: "vertical",
      gestureDirection: "vertical",
      smooth: true,
      mouseMultiplier: 1,
      smoothTouch: false,
      touchMultiplier: 2,
      infinite: false,
    });

    function raf(time) {
      lenis.raf(time);
      requestAnimationFrame(raf);
    }

    requestAnimationFrame(raf);

    return () => {
      lenis.destroy();
    };
  }, []);

  return (
    <div className="bg-black min-h-screen text-white selection:bg-white/30 selection:text-white">
      <CustomCursor />
      <Navbar />
      
      <main>
        <HeroSection />
        <FeaturesSection />
        <CardsSection />
      </main>
      
      <Footer />
    </div>
  );
}

export default App;
