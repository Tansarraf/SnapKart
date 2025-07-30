import ProductCard from "./Shared/ProductCard";

const products = [
  {
    image: "iphone.jpg",
    productName: "Apple iPhone 13 Pro Max (128GB) - Gold",
    description:
      "The iPhone 13 Pro Max offers exceptional performance with its A15 Bionic chip, stunning Super Retina XDR display, and advanced camera features for breathtaking photos.",
    specialPrice: 720,
    price: 780,
  },
  {
    image: "samsung.jpg",
    productName:
      "Samsung Galaxy S21 5G (Phantom White, 8GB RAM, 128GB Storage)",
    description:
      "Experience the brilliance of the Samsung Galaxy S21 with its vibrant AMOLED display, powerful camera, and sleek design that fits perfectly in your hand.",
    specialPrice: 699,
    price: 799,
  },
  {
    image: "pixel.jpg",
    productName:
      "Google Pixel 6 Pro 5G (Stromy Black, 12GB RAM, 128GB Storage)",
    description:
      "Introducing Pixel 6 Pro, the completely redesigned, fully loaded Google 5G cell phone. With a powerful camera system, next-gen security, and the custom Google Tensor processor, it’s the smartest and fastest Pixel yet.",
    price: 599,
    specialPrice: 400,
  },
];
const About = () => {
  return (
    <div className="max-w-7xl mx-auto px-4 py-8">
      <h1 className="text-slate-800 text-4xl font-bold text-center mb-12">
        About Us
      </h1>
      <div className="flex flex-col lg:flex-row justify-between items-center mb-12">
        <div className="w-full md:w-1/2 text-center md:text-left">
          <p className="text-lg mb-4">
            Welcome to SnapKart — a solo-driven e-commerce destination crafted
            with care, passion, and modern tech. Born from the idea of making
            quality, stylish, and affordable products accessible to all,
            SnapKart reflects a commitment to a seamless shopping experience.
            Every product you see is personally selected to ensure it meets
            real-world needs without compromising on value. Built by one,
            curated for many — this is where premium feel meets personal touch.
          </p>
        </div>
        <div className="w-full md:w-1/2 mb-6 md:mb-0">
          <img
            src="about.jpeg"
            alt="About Us"
            className="w-full h-auto rounded-lg shadow-lg transform transition-transform duration-300 hover:scale-105"
          />
        </div>
      </div>
      <div className="py-7 space-y-8">
        <h1 className="text-slate-800 text-4xl font-bold text-center mb-12">
          Our Products
        </h1>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {products.map((product, index) => (
            <ProductCard
              key={index}
              image={product.image}
              productName={product.productName}
              description={product.description}
              specialPrice={product.specialPrice}
              price={product.price}
              about
            />
          ))}
        </div>
      </div>
    </div>
  );
};

export default About;
